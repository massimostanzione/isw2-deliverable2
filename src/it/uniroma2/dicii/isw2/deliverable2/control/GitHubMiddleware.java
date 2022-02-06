package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.GitWorkingCopy;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import it.uniroma2.dicii.isw2.deliverable2.io.CSVExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.utils.CollectionSorter;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class as a "middleware layer" with the GitHub environment, to handle and
 * manage working copies of the analyzed projects.
 */
public class GitHubMiddleware {
    public static final String DEFAULT_GIT_WORKINGCOPY_PATH = "/tmp/isw2-deliverable2";
    private static GitWorkingCopy ret;

    private static Logger log = LoggerInst.getSingletonInstance();

    private GitHubMiddleware() {
    }

    /**
     * Download a project and instantiate local working copy, if it does not already exists.
     *
     * @param projName project name
     * @param remote   URL of the project
     * @return reference to the project's local working copy
     */
    public static GitWorkingCopy createWorkingCopy(String projName, String remote, String version) {
        ret = new GitWorkingCopy();
        log.info(() -> "Checking for working copy...");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DEFAULT_GIT_WORKINGCOPY_PATH).append("/").append(projName);
            if (!Files.exists(Path.of(sb.toString()))) {
                log.info(() -> "- Creating working copy. It may take a while, please wait...");
                Git.cloneRepository()
                        .setURI(remote).setBranch(version).setDirectory(new File(sb.toString())).call();
                log.info(() -> "- Working copy is ready.");
            } else {
                log.info(() -> "- Working copy already exists locally at " + sb.toString() + ".");
            }
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            sb.append("/.git");
            Repository repo = builder
                    .setGitDir(new File(sb.toString()))
                    .setMustExist(true).build();
            Git git = new Git(repo);
            ret.setGit(git);
        } catch (GitAPIException | IOException e) {
            log.severe(e.getMessage());
        }
        return ret;
    }

    /**
     * Extract raw commits from the working copy, and convert them in a more manageable
     * <code>Commit</code> form.
     *
     * @param projName project name
     * @return list of "non-raw" commits related to the project
     */
    public static List<Commit> extractCommits(String projName) {
        log.info(() -> "Extracting commits...");
        List<Commit> commits = new ArrayList<>();
        // Extract raw RevCommit iterator
        try {
            Iterator<RevCommit> iter = ret.getGit().log().all().call().iterator();
            // Iterate through raw commits and convert them
            while (iter.hasNext()) {
                RevCommit iteratedCommit = iter.next();
                if (iteratedCommit.getParentCount() > 0) {
                    Commit c = new Commit(iteratedCommit);
                    commits.add(c);
                }
            }
        } catch (RevisionSyntaxException | GitAPIException | IOException e) {
            log.severe(e.getMessage());
        }

        // Sort commit by their date
        try {
            CollectionSorter.sort(commits, Commit.class.getDeclaredMethod("getDate"));
        } catch (NoSuchMethodException | SecurityException e) {
            log.severe(e.getMessage());
        }

        // For each commit, fetch touched classes
        for (Integer ci = 0; ci < commits.size(); ci++) {
            Commit analyzedCommit = commits.get(ci);
            List<MeasuredClass> touchedFiles = fetchClassesTouchedByCommit(ret.getGit(),
                    analyzedCommit.getReferredRawCommit().getParent(0), analyzedCommit.getReferredRawCommit());
            analyzedCommit.setTouchedFiles(touchedFiles);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(commits, "/output/" + projName + "/inspection/commits.csv");
        log.info(() -> "- " + commits.size() + " commits found.");
        return commits;
    }

    /**
     * Fetch all the classes touched by a commit, and instantiate a list of more manageable
     * <code>MeasuredClass</code> objects.
     *
     * @param git        git object
     * @param prevCommit current commit
     * @param fixCommit  next commit
     * @return list of all the classes, in a more <code>MeasuredClass</code> manageable way.
     */
    private static List<MeasuredClass> fetchClassesTouchedByCommit(Git git, RevCommit prevCommit, RevCommit fixCommit) {
        log.finer(() -> "Retrieving touched java classes for commit " + prevCommit.getId());
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(git.getRepository());
        df.setDiffComparator(RawTextComparator.DEFAULT);
        List<MeasuredClass> files = new ArrayList<>();
        List<DiffEntry> diffEntryList = null;
        try {
            diffEntryList = df.scan(prevCommit, fixCommit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (DiffEntry diff : diffEntryList) {
            // Discard all non-java files
            if (diff.getNewPath().contains(".java"))
                files.add(new MeasuredClass(diff.getNewPath()));
        }
        log.finer(() -> "Commmit " + prevCommit.getId() + ": " + files.size() + " java classes touched.");
        return files;
    }

    /**
     * Find the GitHub hash ID for the object named by <code>name</code>.
     *
     * @param name object ID
     * @param wc   working copy
     * @return the GitHub hash ID related to the provided object, or <code>null</code> if not found
     */
    public static String findGitHubHashID(String name, GitWorkingCopy wc) {
        List<Ref> versionTags = wc.getVersionTags();
        for (Ref tag : versionTags) {
            if (tag.toString().contains(name)) {
                ObjectId objId = new ObjectId(0,0,0,0,0);
                try {
                    objId = tag.getObjectId();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return objId.getName();
            }
        }
        return null;
    }
}
