package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A single ticket.
 */
public class Ticket extends ExportableAsDatasetRecord {
    private String ID;
    private Date creationTimestamp;
    private Date fixTimestamp;
    private List<Commit> commitList;

    public Ticket(String key, String creat, String res) {
        this.ID = key;
        try {
            this.creationTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(creat);
            this.fixTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getFixTimestamp() {
        return fixTimestamp;
    }

    public void setFixTimestamp(Date fixTimestamp) {
        this.fixTimestamp = fixTimestamp;
    }

    public List<Commit> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<Commit> commitList) {
        this.commitList = commitList;
    }

    /**
     * Obtain the date of the last commit referring the ticket.
     *
     * @return the last commit date, or null if not any
     */
    public Date getLastCommitDate() {
        List<Commit> commitList = this.commitList;
        if (commitList != null) {
            Commit first = commitList.get(0);
            Date last = first.getDate();
            for (Integer j = 0; j < commitList.size(); j++) {
                if (commitList.get(j).getDate() != null && commitList.get(j).getDate().after(last)) {
                    last = commitList.get(j).getDate();
                }
            }
            return last;
        } else {
            return null;
        }
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("ID", "Created (JIRA)", "Fixed (JIRA)");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.ID, this.creationTimestamp, this.fixTimestamp);
        return this.datasetRecord;
    }
}
