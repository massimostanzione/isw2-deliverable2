package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.control.JIRAAffectedVersionsCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Single entry of a graphic tool to show an human-readable overview of the lifecycles of all bugs.
 */
public class GraphicBugLifecycleVisualizerRecord extends ExportableAsDatasetRecord {
    private List<Version> versionList;
    private String bugId;
    private List<String> values = new ArrayList<>();
    private List<JIRAAffectedVersionsCheck> jiraCheck;

    public GraphicBugLifecycleVisualizerRecord(List<Version> versionList) {
        super();
        this.versionList = versionList;
    }

    public List<JIRAAffectedVersionsCheck> getJIRACheck() {
        return jiraCheck;
    }

    public void setJIRACheck(List<JIRAAffectedVersionsCheck> jIRACheck) {
        jiraCheck = jIRACheck;
    }


    public void setID(String id) {
        this.bugId = id;

    }

    public void appendLabel(String val) {
        this.values.add(val);

    }

    private List<String> getVersionsSortedIDs() {
        List<String> ret = new ArrayList<>();
        for (Version v : this.versionList) {
            ret.add(v.getSortedID().toString());
        }
        return ret;
    }

    private String getJIRACheckAsString() {
        StringBuilder sb = new StringBuilder();
        for (Integer i = 0; i < this.jiraCheck.size(); i++) {
            String error = this.jiraCheck.get(i).toString();
            sb.append(i == this.jiraCheck.size() - 1 ? error : error + "*");
        }
        return sb.toString();
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("ReferringTicketID", this.getVersionsSortedIDs(), "JIRACheck");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.bugId, this.values, this.getJIRACheckAsString());
        return this.datasetRecord;
    }
}
