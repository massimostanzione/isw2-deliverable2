package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import it.uniroma2.dicii.isw2.deliverable2.entities.Ticket;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import it.uniroma2.dicii.isw2.deliverable2.utils.CollectionSorter;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for commit handling.
 */
public class CommitHandler {
    private static Logger log = LoggerInst.getSingletonInstance();

    private CommitHandler() {
    }

    /**
     * Given a version, retrieve all the commits related to it.
     *
     * @param v           version
     * @param commitList  list of commits, where related commits are searched
     * @param versionList list of all versions
     * @return list of commits related to version <code>v</code>
     */
    public static List<Commit> getCommitsRelatedToVersion(Version v, List<Commit> commitList, List<Version> versionList) {
        List<Commit> ret = new ArrayList<>();
        Date first = v.getVersionDate();
        Date last = versionList.get(v.getSortedID()).getVersionDate();
        for (Commit c : commitList) {
            Date commitDateTime = c.getReferredRawCommit().getAuthorIdent().getWhen();
            if (commitDateTime.before(last) && (commitDateTime.equals(first) || commitDateTime.after(first)))
                ret.add(c);
        }
        log.fine(() -> "- Version " + v.getName() + ": " + ret.size() + " commits found.");
        return ret;
    }

    /**
     * Fetch all the unique classes touched by all the project commits
     *
     * @param commitList list of the project commits
     * @return list of all the classes touched in the project
     */
    public static List<MeasuredClass> getAllClasses(List<Commit> commitList) {
        log.info(() -> "Fetching all the classes...");
        List<MeasuredClass> allClasses = new ArrayList<>();
        for (Commit c : commitList) {
            for (MeasuredClass mc : c.getTouchedFiles()) {
                if (!(allClasses.stream().anyMatch(o -> o.getName().contains(mc.getName()))))
                    allClasses.add(mc);
            }
        }
        try {
            CollectionSorter.sort(allClasses, MeasuredClass.class.getDeclaredMethod("getName"));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        log.info(() -> "- " + allClasses.size() + " unique classes found. They will be filtered later.");
        return allClasses;
    }

    /**
     * Given a ticket and a commit list, return a sublist of the commits that are related to the ticket
     *
     * @param ticket     ticket
     * @param commitList list of commits
     * @return sublist of <code>commitList</code> with commits related to <code>ticket</code>
     */
    public static List<Commit> fetchCommitsRelatedToTicket(Ticket ticket, List<Commit> commitList) {
        List<Commit> ret = new ArrayList<>();
        for (Commit iteratedCommit : commitList) {
            if (iteratedCommit.getCommitMsg().contains(ticket.getId() + (":"))
                    || iteratedCommit.getCommitMsg().contains(ticket.getId() + " ")
                    || iteratedCommit.getCommitMsg().contains(ticket.getId() + "]")) {
                ret.add(iteratedCommit);
            }
        }
        return ret;
    }
}
