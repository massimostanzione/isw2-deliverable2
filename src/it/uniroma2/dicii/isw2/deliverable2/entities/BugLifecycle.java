package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.enumerations.JIRAAffectedVersionsCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Lifecycle of a bug.
 */
public class BugLifecycle {
    private Version iv;
    private Version ov;
    private Version fv;
    private List<Version> avs;
    private List<JIRAAffectedVersionsCheck> jiraCheck = new ArrayList<>();
    private float proportionContribute;
    private boolean wasIVPredictionNeeded;

    public Version getIV() {
        return iv;
    }

    public void setIV(Version iV) {
        iv = iV;
    }

    public Version getOV() {
        return ov;
    }

    public void setOV(Version oV) {
        ov = oV;
    }

    public Version getFV() {
        return fv;
    }

    public void setFV(Version fV) {
        fv = fV;
    }

    public void setAVs(List<Version> avs) {
        this.avs = avs;
    }

    public List<Version> getAVs() {
        return avs;
    }

    public List<JIRAAffectedVersionsCheck> getJIRACheck() {
        return jiraCheck;
    }

    public void setJIRACheck(List<JIRAAffectedVersionsCheck> jIRACheck) {
        jiraCheck = jIRACheck;
    }

    public void setProportionContribute(float prop) {
        this.proportionContribute = prop;
    }

    public float getProportionContribute() {
        return this.proportionContribute;
    }

    public void setIVPredictionNeeded(boolean val) {
        this.wasIVPredictionNeeded = val;
    }

    public boolean isIVPredicionNeeded() {
        return this.wasIVPredictionNeeded;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Version v : this.avs) {
            sb.append(v.getSortedID()).append(" ");
        }
        sb.append(this.fv.getSortedID());
        return sb.toString();
    }
}
