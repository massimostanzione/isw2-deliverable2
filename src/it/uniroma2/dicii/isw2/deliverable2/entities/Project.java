package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.control.TicketBugHandler;
import it.uniroma2.dicii.isw2.deliverable2.enumerations.LabelingMethod;

import java.util.List;

/**
 * A project that is being analyzed.
 */
public class Project {
    private String name;
    private String gitHubURL;
    private final String gitHubVersion;
    private List<Version> versionList;
    private List<Bug> bugList;
    private GitWorkingCopy workingCopy;
    private List<Commit> commitList;

    public Project(String name, String gitHubURL, String gitHubVersion) {
        super();
        this.name = name;
        this.gitHubURL = gitHubURL;
        this.gitHubVersion = gitHubVersion;
    }

    public void initializeBugList(LabelingMethod avPredMethod) {
        this.bugList = TicketBugHandler.fetchProjectBugs(this.name, this.commitList, this.versionList, avPredMethod);
    }

    public List<Commit> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<Commit> commitList) {
        this.commitList = commitList;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGitHubURL() {
        return gitHubURL;
    }

    public void setGitHubURL(String gitHubURL) {
        this.gitHubURL = gitHubURL;
    }

    public List<Version> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<Version> versionList) {
        this.versionList = versionList;
    }

    public List<Bug> getBugList() {
        return bugList;
    }

    public void setBugList(List<Bug> bugList) {
        this.bugList = bugList;
    }

    public GitWorkingCopy getWorkingCopy() {
        return workingCopy;
    }

    public void setWorkingCopy(GitWorkingCopy workingCopy) {
        this.workingCopy = workingCopy;
    }

    public String getGitHubVersion() {
        return this.gitHubVersion;
    }
}
