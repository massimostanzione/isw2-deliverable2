package it.uniroma2.dicii.isw2.deliverable2.entities;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Iterator;
import java.util.List;

/**
 * A single Git working copy.
 */
public class GitWorkingCopy {
    private String path;
    private Git git;

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return ret;
    }

    public Repository getRepository() {
        return this.git.getRepository();
    }
}
