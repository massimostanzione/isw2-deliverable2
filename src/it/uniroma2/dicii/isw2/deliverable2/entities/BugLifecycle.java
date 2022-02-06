package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.control.JIRAAffectedVersionsCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Lifecycle of a bug.
 */
public class BugLifecycle {
    private Version IV;
    private Version OV;
    private Version FV;
    private List<Version> AVs;
    private List<JIRAAffectedVersionsCheck> JIRACheck = new ArrayList<>();
    private float proportionContribute;
    private boolean wasIVPredictionNeeded;

    public Version getIV() {
        return IV;
    }

    public void setIV(Version iV) {
        IV = iV;
    }

    public Version getOV() {
        return OV;
    }

    public void setOV(Version oV) {
        OV = oV;
    }

    public Version getFV() {
        return FV;
    }

    public void setFV(Version fV) {
        FV = fV;
    }

    public void setAVs(List<Version> avs) {
        this.AVs = avs;
    }

    public List<Version> getAVs() {
        return AVs;
    }

    public List<JIRAAffectedVersionsCheck> getJIRACheck() {
        return JIRACheck;
    }

    public void setJIRACheck(List<JIRAAffectedVersionsCheck> jIRACheck) {
        JIRACheck = jIRACheck;
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
        String ret = "";
        for (Version v : this.AVs) {
            ret += v.getSortedID() + " ";
        }
        ret += this.FV.getSortedID();
        return ret;
    }
}
