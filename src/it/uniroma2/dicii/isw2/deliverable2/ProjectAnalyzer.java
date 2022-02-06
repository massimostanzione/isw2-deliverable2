package it.uniroma2.dicii.isw2.deliverable2;

import it.uniroma2.dicii.isw2.deliverable2.control.CommitHandler;
import it.uniroma2.dicii.isw2.deliverable2.control.GitHubMiddleware;
import it.uniroma2.dicii.isw2.deliverable2.control.MLAnalysis;
import it.uniroma2.dicii.isw2.deliverable2.control.VersionHandler;
import it.uniroma2.dicii.isw2.deliverable2.entities.*;
import it.uniroma2.dicii.isw2.deliverable2.enumerations.LabelingMethod;
import it.uniroma2.dicii.isw2.deliverable2.io.ARFFExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.io.CSVExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Central "control" class, orchestrating all the operations.
 */
public class ProjectAnalyzer {
    private static Logger log = LoggerInst.getSingletonInstance();
    private final Project p;
    private final LabelingMethod avPredMethod;
    private final List<TouchedClassesInspection> inspList = new ArrayList<>();
    List<MeasuredClass> allClasses;
    private
    String root = "/output/";

    public ProjectAnalyzer(Project p, LabelingMethod avPredMethod) {
        this.p = p;
        this.avPredMethod = avPredMethod;
    }

    /**
     * The main program workflow.
     *
     * @throws Exception
     */
    public void run() throws Exception {
        log.info(() -> "Running analysis for " + p.getName() + ".");
        initializeProjectData();
        p.initializeBugList(this.avPredMethod);
        measureClasses();
        prepareMLDatasets();
        MLAnalysis.performWekaMLAnalysis(p);
        log.info(() -> "Finished.");
    }

    /**
     * Data setup: generate working copy and fetch commit and version list for the project.
     */
    private void initializeProjectData() {
        p.setWorkingCopy(GitHubMiddleware.createWorkingCopy(p.getName(), p.getGitHubURL(), p.getGitHubVersion()));
        p.setCommitList(GitHubMiddleware.extractCommits(p.getName()));
        p.setVersionList(VersionHandler.fetchProjectVersions(p.getName(), p.getWorkingCopy()));
    }

    /**
     * Analyze all the classes, measuring them and producing intermediate dataset.
     */
    public void measureClasses() {
        String projName = p.getName();
        GitWorkingCopy workingCopy = p.getWorkingCopy();
        List<Version> versionList = p.getVersionList();
        List<Commit> commitList = p.getCommitList();
        this.allClasses = CommitHandler.getAllClasses(p.getCommitList());

        RevWalk revWalk = new RevWalk(workingCopy.getGit().getRepository());
        revWalk.sort(RevSort.TOPO);
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(workingCopy.getRepository());
        df.setContext(0);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        // Iterate through versions, commits, diffs
        for (Integer i = 0; i < (versionList.size() / 2); i++) {
            Version v = versionList.get(i);
            List<Commit> relatedCommitList = CommitHandler.getCommitsRelatedToVersion(v, commitList, versionList);// EFFICIENCY!
            for (Integer ci = 0; ci < relatedCommitList.size(); ci++) {
                Commit iteratedCommit = relatedCommitList.get(ci);
                List<DiffEntry> diffEntries = new ArrayList<>();
                RevTree currTree = null;
                try {
                    currTree = iteratedCommit.getReferredRawCommit().getTree();
                    diffEntries = df.scan(iteratedCommit.getReferredRawCommit().getParent(0).getTree(), currTree);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (DiffEntry diff : diffEntries) {
                    if (diff.getNewPath().contains(".java")) {
                        examineClass(diff, v, currTree, workingCopy, df, iteratedCommit);
                    }
                }
            }
        }
        CSVExporterPrinter.export(prepareDataset(allClasses), root + projName + "/dataset/dataset.csv");
        CSVExporterPrinter.getSingletonInstance().convertAndExport(inspList, root + projName + "/inspection/touchedClassesInsp.csv");
    }

    private void examineClass(DiffEntry diff, Version v, RevTree currTree, GitWorkingCopy workingCopy,
                              DiffFormatter df, Commit iteratedCommit) {
        for (MeasuredClass mc : allClasses) {
            // if class is touched by commit
            if (mc.getName().equals(diff.getNewPath())) {
                Measure m = mc.atVersion(v) == null ? new Measure(mc.getName(), v) : mc.atVersion(v);
                Repository repository = workingCopy.getGit().getRepository();
                TreeWalk treeWalk = new TreeWalk(repository);
                try {
                    treeWalk.addTree(currTree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(diff.getNewPath()));
                    if (!treeWalk.next()) {
                        treeWalk.close();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = null;
                try {
                    loader = repository.open(objectId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Integer addedLOCs = 0;
                Integer removedLOCs = 0;
                try {
                    for (Edit edit : df.toFileHeader(diff).toEditList()) {
                        addedLOCs += edit.getEndB() - edit.getBeginB();
                        removedLOCs += edit.getEndA() - edit.getBeginA();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Compute/update metrics
                m.computeMetrics(mc, v, iteratedCommit.getTouchedFiles().size(),
                        loader, df, diff, addedLOCs, removedLOCs,
                        iteratedCommit.getReferredRawCommit().getAuthorIdent().getName());
                if (Boolean.FALSE.equals(m.isBuggy())) {
                    m.setBuggy(checkBugginess(mc, iteratedCommit));
                }
                if (mc.atVersion(v) == null) {
                    mc.getMeasures().add(m);
                }
                treeWalk.close();
                break;
            }
        }
    }

    /**
     * Produce intermediate datasets, to be used in ML analysis.
     */
    private void prepareMLDatasets() {
        log.info(() -> "Preparing datasets for the subsqeuent ML Weka analysis...");
        List<Version> versionList = p.getVersionList();
        String projName = p.getName();
        List<Measure> testing = new ArrayList<>();
        List<Measure> training = new ArrayList<>();
        for (Integer i = 0; i <= (versionList.size() / 2); i++) {
            for (MeasuredClass mc : allClasses) {
                if (mc.atVersion(versionList.get(i)) != null) {
                    testing.add(mc.atVersion(versionList.get(i)));
                }
            }
            CSVExporterPrinter.getSingletonInstance().convertAndExport(testing,
                    root + projName + "/dataset/testing/TE" + versionList.get(i).getSortedID() + ".csv");
            ARFFExporterPrinter.getSingletonInstance().convertAndExport(projName, testing,
                    root + projName + "/dataset/testing/TE" + versionList.get(i).getSortedID() + ".arff");
            for (Integer j = 0; j <= i; j++) {
                for (MeasuredClass mc : allClasses) {
                    if (mc.atVersion(versionList.get(j)) != null) {
                        training.add(mc.atVersion(versionList.get(j)));
                    }
                }
            }
            CSVExporterPrinter.getSingletonInstance().convertAndExport(training,
                    root + projName + "/dataset/training/TR" + versionList.get(i).getSortedID() + ".csv");
            ARFFExporterPrinter.getSingletonInstance().convertAndExport(projName, training,
                    root + projName + "/dataset/training/TR" + versionList.get(i).getSortedID() + ".arff");
            training.clear();
            testing.clear();
        }
        log.info(() -> "- Done.");
    }

    /**
     * Check if a class is buggy, given a specific commit:
     * this method is only called for a previously specified commit, that touched specific classes
     * in a specific version, so it is sufficient to check if that commit is involved in a bug.
     *
     * @param mc class to be checked (for inspection only)
     * @param c  commit
     * @return true if <code>mc</code> is buggy (touched by <code>c</code>), checking with the list of bugs.
     */
    private Boolean checkBugginess(MeasuredClass mc, Commit c) {
        List<Version> versionList = p.getVersionList();
        for (Bug b : p.getBugList()) {
            if (b.getBugLifecycle().getFV().getSortedID() <= (versionList.size() / 2)) {
                for (Commit check : b.getTicket().getCommitList()) {
                    // If commit is referring this specific bug's ticket
                    if (check.getCommitID().equals(c.getCommitID())) {
                        // Here we already know that the class is buggy
                        // (this specific commit is involved in a bug).
                        // Next method is for inspection purposes only.
                        compileBugInspection(b, check, mc, versionList);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void compileBugInspection(Bug b, Commit check, MeasuredClass mc, List<Version> versionList) {
        for (Version ve : versionList) {
            for (Version bugAV : b.getBugLifecycle().getAVs()) {
                if (ve.getSortedID().equals(bugAV.getSortedID())) {
                    TouchedClassesInspection insp = new TouchedClassesInspection();
                    insp.setB(b);
                    insp.setBl(b.getBugLifecycle());
                    insp.setC(check);
                    insp.setMc(mc);
                    insp.setV(ve);
                    inspList.add(insp);
                }
            }
        }
    }

    /**
     * Prepare information about measured classes for subsequent analysis.
     *
     * @param headFiles list of all the measured classes
     * @return information about the classed, in a more "CSV-friendly" way
     */
    private List<List<String>> prepareDataset(List<MeasuredClass> headFiles) {
        List<Measure> ret1 = new ArrayList<>();
        for (MeasuredClass mc : headFiles) {
            for (Measure m : mc.getMeasures()) {
                ret1.add(m);
            }
        }
        List<List<String>> ret = CSVExporterPrinter.getSingletonInstance().convertToCSVExportable(ret1);
        Collections.sort(ret, (o1, o2) -> {
            try {
                return Integer.valueOf(o1.get(0)).compareTo(Integer.valueOf(o2.get(0)));
            } catch (NumberFormatException e) {
                // When title is parsed
                return 0;
            }
        });
        return ret;
    }
}
