package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * A single ticket.
 */
public class Ticket extends ExportableAsDatasetRecord {
    private String id;
    private Date creationTimestamp;
    private Date fixTimestamp;
    private List<Commit> commitList;

    private static final Logger log = LoggerInst.getSingletonInstance();

    public Ticket(String key, String creat, String res) {
        this.id = key;
        try {
            this.creationTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(creat);
            this.fixTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(res);
        } catch (ParseException e) {
            log.severe(e.getMessage());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String iD) {
        id = iD;
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
        List<Commit> checkingCommitList = this.commitList;
        if (checkingCommitList != null) {
            Commit first = checkingCommitList.get(0);
            Date last = first.getDate();
            for (Integer j = 0; j < checkingCommitList.size(); j++) {
                if (checkingCommitList.get(j).getDate() != null && checkingCommitList.get(j).getDate().after(last)) {
                    last = checkingCommitList.get(j).getDate();
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
        this.setDatasetRecord(this.id, this.creationTimestamp, this.fixTimestamp);
        return this.datasetRecord;
    }
}
