package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.BugLifecycle;
import it.uniroma2.dicii.isw2.deliverable2.entities.Ticket;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import it.uniroma2.dicii.isw2.deliverable2.enumerations.LabelingMethod;
import it.uniroma2.dicii.isw2.deliverable2.exceptions.VersionException;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class to manage every kind of problems related to determining a lifecycle of a bug
 */
public class BugLifecycleFixer {
    private static final Logger log = LoggerInst.getSingletonInstance();

    /**
     * Examine a lifecycle of a bug and check for any error that can be discovered when
     * determining the lifecycle of a bug.
     *
     * @param bl lifecycle of a bug
     * @return list of <code>JIRAAffectedVersionsCheck</code> problems, or a list including "OK" if not any.
     * @see JIRAAffectedVersionsCheck
     */
    public static List<JIRAAffectedVersionsCheck> checkConsistency(BugLifecycle bl) {
        List<JIRAAffectedVersionsCheck> errors = new ArrayList<>();
        if (bl.getAVs().get(0).getVersionDate().after(bl.getOV().getVersionDate()))
            errors.add(JIRAAffectedVersionsCheck.IV_AFTER_OV);
        for (Version v : bl.getAVs()) {
            if (v.getVersionDate().after(bl.getFV().getVersionDate())) {
                errors.add(JIRAAffectedVersionsCheck.AV_AFTER_FV);
                break;
            }
        }
        for (Version v : bl.getAVs()) {
            if (v.getVersionDate().equals(bl.getFV().getVersionDate())) {
                errors.add(JIRAAffectedVersionsCheck.FV_AS_AV);
                break;
            }
        }
        for (Integer i = bl.getIV().getSortedID(); i <= bl.getFV().getSortedID() - 1; i++) {
            Boolean found = false;
            for (Version v : bl.getAVs()) {
                if (v.getSortedID() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                errors.add(JIRAAffectedVersionsCheck.AVS_NOT_CONSISTENT);
                break;
            }
        }
        if (errors.isEmpty())
            errors.add(JIRAAffectedVersionsCheck.OK);
        return errors;
    }

    /**
     * When LabelingMethod#SIMPLE is enabled, fill AVs
     *
     * @param bl          lifecycle of a bug
     * @param versionList list of versions to be searched in
     * @return lifecycle of a bug, with AVs filled in a <code>SIMPLE</code> way.
     */
    public static BugLifecycle fillSimpleLikeAVs(BugLifecycle bl, List<Version> versionList) {
        bl.setAVs(VersionHandler.getVersionsBetween(bl.getIV(), bl.getFV(), versionList));
        return bl;
    }

    /**
     * If FV are found into the AVs, remove it.
     *
     * @param bl lifecycle of a bug
     * @return lifecycle of a bug, with FV removed from AVs.
     */
    public static BugLifecycle removeFVfromAVs(BugLifecycle bl) {
        for (Version v : bl.getAVs()) {
            if (v.getVersionDate().equals(bl.getFV().getVersionDate())) {
                bl.getAVs().remove(v);
                return bl;
            }
        }
        return bl;
    }

    /**
     * Actively compute a lifecycle of a bug, based on ticket's timeline.
     * Bugs discarded:
     * (a) bugs with related ticket that has no fix date;
     * (b) bugs whose lifecycle is not coherent.
     *
     * @param proportionAvgNum for the computing of proportion
     * @param n                for the computing of proportion
     * @param JIRAVersions     list of versions to be searched in
     * @param iteratedTicket   ticket related to the bug
     * @param versionList      all the project's versions
     * @param avPredMethod     labeling method
     * @return the lifecycle of the bug
     * @throws Exception
     * @throws VersionException
     */
    public static BugLifecycle computeBugLifecycle(Double proportionAvgNum, Integer n, List<Version> JIRAVersions, Ticket iteratedTicket,
                                                   List<Version> versionList, LabelingMethod avPredMethod) throws Exception, VersionException {
        BugLifecycle bl = new BugLifecycle();
        Version iv = null, ov = null, fv = null;
        List<Version> av = new ArrayList<>();

        // (1) Compute FV and OV
        fv = VersionHandler.getNextVersionByDate(iteratedTicket.getLastCommitDate(), versionList);
        if (fv == null) {
            // Discard commit (a)
            log.fine(() -> "Discarded 1 bug with no fix date.");
            return null;
        }
        ov = VersionHandler.getNextVersionByDate(iteratedTicket.getCreationTimestamp(), versionList);
        if (ov.equals(fv)) {
            /* Ticket was created and solved in the same version.
            It does not imply that IV does not exists, it can be previous indeed.
            So, set it as the "current" version, in such a way that:
            - it is FV-1;
            - it still is AV;
            - it is the nearest to the ticket opening.
             */
            ov = VersionHandler.getCurrentVersionByDate(iteratedTicket.getCreationTimestamp(), versionList);
        }
        bl.setOV(ov);
        bl.setFV(fv);

        // (2) check reliability of JIRA versions
        if (JIRAVersions.size() > 0) {
            iv = JIRAVersions.get(0);
            bl.setIV(iv);
            av.addAll(JIRAVersions);
            bl.setAVs(av);
            bl.setJIRACheck(BugLifecycleFixer.checkConsistency(bl));
            Boolean needPrediction = false;
            for (Integer i = 0; i < bl.getJIRACheck().size(); i++) {
                JIRAAffectedVersionsCheck error = bl.getJIRACheck().get(i);
                switch (error) {
                    case OK:
                        // Best case: lifecycle is consistent, not even needs a prediction.
                        return bl;
                    case AVS_NOT_CONSISTENT:
                    case AV_AFTER_FV:
                        bl = BugLifecycleFixer.fillSimpleLikeAVs(bl, versionList);
                        break;
                    case FV_AS_AV:
                        bl = BugLifecycleFixer.removeFVfromAVs(bl);
                        break;
                    case IV_AFTER_OV:
                        // Worst case: it is not possible, in any way, to obtain information from JIRA.
                        bl.setAVs(null);
                        needPrediction = true;
                        break;
                    case NOT_REPORTED:
                        // Even worse: if the version has not been reported,
                        // the bug must have been already discarded.
                        throw new Exception("Bug with no version reported, but not discarded.");
                    default:
                        throw new Exception("Non-trivial problem with JIRA analysis occurred.");
                }
                if (needPrediction) {
                    bl.setIVPredictionNeeded(true);
                    bl.setProportionContribute(0);
                    break;
                }
            }
            if (!needPrediction)
                bl.setIVPredictionNeeded(false);
            if (fv.getSortedID() > ov.getSortedID() && ov.getSortedID() > iv.getSortedID()) {
                bl.setProportionContribute((float) (fv.getSortedID() - iv.getSortedID()) / (fv.getSortedID() - ov.getSortedID()));
                return bl;
            }
        }
        if (bl.getJIRACheck().isEmpty())
            bl.getJIRACheck().add(JIRAAffectedVersionsCheck.NOT_REPORTED);

        // (3) Prediction
        // (If no JIRA version reported or if there are errors such that it is not possible
        // to obtain AV from JIRA in any way)
        switch (avPredMethod) {
            case SIMPLE:
                /*
                 * (i) We set IV equal to OV. (ii) For each defect, we label each version before
                 * the IV as not affected. We label each version from the IV to the FV as
                 * affected. The FV is labeled not affected.
                 */
                if (ov != null) {
                    iv = ov;
                    av = VersionHandler.getVersionsBetween(iv, fv, versionList);
                }
                break;
            case PROPORTION_INCREMENTAL:
                int prediction = (int) (fv.getSortedID() - ((fv.getSortedID() - ov.getSortedID()) * (proportionAvgNum / n)));
                iv = VersionHandler.getVersionBySortedID(prediction > 0 ? prediction : 1);
                if (iv == null) return null;
                av = VersionHandler.getVersionsBetween(iv, fv, versionList);
                break;
            default:
                throw new Exception("Not valid labeling method for " + avPredMethod);
        }

        // (4) Final coherence check
        if (fv.getSortedID() < iv.getSortedID() || ov.getSortedID() < iv.getSortedID()) {
            //Discard bug: (b)
            return null;
        }
        bl.setIV(iv);
        bl.setAVs(av);
        return bl;
    }
}
