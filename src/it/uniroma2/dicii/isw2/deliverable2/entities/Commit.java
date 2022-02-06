package it.uniroma2.dicii.isw2.deliverable2.entities;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Date;
import java.util.List;

/**
 * A single commit.
 * This is a separated entity with respect to <code>RevCommit</code> from Git,
 * free of all the unnecessary Git details and easier to manage.
 * An association with the corresponding <code>RevCommit</code> is always preserved.
 */
public class Commit extends ExportableAsDatasetRecord {
    private String commitID;
    private String commitMsg;
    private Date date;
    private List<MeasuredClass> touchedFiles;
    private RevCommit referredRawCommit;
    private Boolean referred = false;

    public Commit(RevCommit iteratedCommit) {
        this.referredRawCommit = iteratedCommit;
        this.commitID = this.referredRawCommit.getName();
        this.commitMsg = this.referredRawCommit.getFullMessage();
        this.date = iteratedCommit.getAuthorIdent().getWhen();
    }

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<MeasuredClass> getTouchedFiles() {
        return touchedFiles;
    }

    public void setTouchedFiles(List<MeasuredClass> touchedFiles) {
        this.touchedFiles = touchedFiles;
    }

    public Boolean isReferred() {
        return referred;
    }

    public void setReferred(Boolean referred) {
        this.referred = referred;
    }

    public RevCommit getReferredRawCommit() {
        return referredRawCommit;
    }

    public void setReferredRawCommit(RevCommit referredRawCommit) {
        this.referredRawCommit = referredRawCommit;
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("ID", "Date", "#TouchedClasses");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.commitID, this.date, this.touchedFiles.isEmpty() ? 0 : this.touchedFiles.size());
        return this.datasetRecord;
    }
}
