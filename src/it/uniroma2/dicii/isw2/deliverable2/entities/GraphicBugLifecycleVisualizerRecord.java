package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.control.JIRAAffectedVersionsCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Single entry of a graphic tool to show an human-readable overview of the lifecycles of all bugs.
 */
public class GraphicBugLifecycleVisualizerRecord extends ExportableAsDatasetRecord {
    private List<Version> versionList;
    private List<Bug> bugList;
    private String bugId;
    private List<String> values = new ArrayList<>();
    private List<JIRAAffectedVersionsCheck> JIRACheck;

    public GraphicBugLifecycleVisualizerRecord(List<Version> versionList, List<Bug> bugList) {
        super();
        this.versionList = versionList;
        this.bugList = bugList;
    }

    public List<JIRAAffectedVersionsCheck> getJIRACheck() {
        return JIRACheck;
    }

    public void setJIRACheck(List<JIRAAffectedVersionsCheck> jIRACheck) {
        JIRACheck = jIRACheck;
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
        String ret = "";
        for (Integer i = 0; i < this.JIRACheck.size(); i++) {
            String error = this.JIRACheck.get(i).toString();
            ret += i == this.JIRACheck.size() - 1 ? error : error + "*";
        }
        return ret;
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
