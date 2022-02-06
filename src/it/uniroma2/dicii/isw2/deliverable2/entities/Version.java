package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.util.Date;
import java.util.List;

/**
 * A single version of a project.
 */
public class Version extends ExportableAsDatasetRecord {
    private Integer sortedID;
    private String name;
    private String hashID;
    private String referredCommit;
    private Date versionDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashID() {
        return hashID;
    }

    public void setHashID(String hashID) {
        this.hashID = hashID;
    }

    public Integer getSortedID() {
        return sortedID;
    }

    public void setSortedID(Integer sortedID) {
        this.sortedID = sortedID;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date releaseDate) {
        this.versionDate = releaseDate;
    }

    public String getReferredCommit() {
        return referredCommit;
    }

    public void setReferredCommit(String referredCommit) {
        this.referredCommit = referredCommit;
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("SortedID", "Name", "Hash", "Version_Date");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.sortedID, this.name, this.hashID, this.versionDate);
        return this.datasetRecord;
    }
}
