package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A single Git working copy.
 */
public class GitWorkingCopy {
    private String path;
    private Git git;
    private static final Logger log = LoggerInst.getSingletonInstance();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public Iterable<RevCommit> getLog() {
        Iterable<RevCommit> ret = null;
        try {
            ret = this.git.log().call();
        } catch (GitAPIException e) {
            log.severe(e.getMessage());
        }
        return ret;
    }

    public Iterator<RevCommit> getLogIterator() {
        Iterator<RevCommit> ret = null;
        ret = getLog().iterator();
        return ret;
    }

    public List<Ref> getVersionTags() {
        List<Ref> ret = null;
        try {
            ret = this.git.tagList().call();
        } catch (GitAPIException e) {
            log.severe(e.getMessage());
        }
        return ret;
    }

    public Repository getRepository() {
        return this.git.getRepository();
    }
}
