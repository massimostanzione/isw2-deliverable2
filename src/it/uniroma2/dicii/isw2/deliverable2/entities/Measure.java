package it.uniroma2.dicii.isw2.deliverable2.entities;

import it.uniroma2.dicii.isw2.deliverable2.metrics.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A container of metrics, representing a single measuration of a class
 */
public class Measure extends ExportableAsDatasetRecord {

    private Version version;
    private String name;
    private final Metric size = new Size();
    private final Metric LOC_touched = new LOC_Touched();
    private final Metric nr = new NR();
    private final Metric nAuth = new NAuth();
    private final Metric LOC_added = new LOC_Added();
    private final Metric max_LOC_added = new Max_LOC_Added();
    private final Metric avg_LOC_added = new Avg_LOC_Added();
    private Metric churn = new Churn();
    private final Metric chgSetSize = new ChangesetSize();
    private Boolean buggy = false;

    public Measure(String name2, Version v) {
        this.name = name2;
        this.version = v;
    }

    public Metric getNr() {
        return nr;
    }

    public Metric getLOC_added() {
        return LOC_added;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isBuggy() {
        return buggy;
    }

    public void setBuggy(Boolean buggy) {
        this.buggy = buggy;
    }

    /**
     * Obtain the single misuration (all the metrics, in a single <code>List</code>).
     *
     * @return list with all the metrics
     */
    public List<Metric> getMetricsAsList() {
        List<Metric> ret = new ArrayList<>();
        ret.add(this.size);
        ret.add(this.nr);
        ret.add(this.nAuth);
        ret.add(this.LOC_touched);
        ret.add(this.LOC_added);
        // Following parameters are dependant on the previous ones.
        ret.add(this.max_LOC_added);
        ret.add(this.avg_LOC_added);
        ret.add(this.churn);
        ret.add(this.chgSetSize);
        return ret;
    }

    /**
     * Obtain a list with metrics names
     *
     * @return list with all the metrics names
     */
    private List<String> datasetNamesToStringList() {
        List<String> ret = new ArrayList<>();
        for (Metric m : this.getMetricsAsList()) {
            ret.add(m.getDatasetName());
        }
        return ret;
    }

    /**
     * Obtain a list with metrics values
     *
     * @return list with all the metrics values
     */
    public List<String> metricsToStringList() {
        List<String> ret = new ArrayList<>();
        for (Metric m : this.getMetricsAsList()) {
            ret.add(m.getDatasetValue());
        }
        return ret;
    }

    /**
     * Update this measure with <code>me</code>'s values.
     *
     * @param me new values
     */
    public void update(Measure me) {
        for (Integer i = 0; i < me.getMetricsAsList().size(); i++) {
            this.getMetricsAsList().get(i).setValue(me.getMetricsAsList().get(i).getValue());
        }
    }

    /**
     * Given a class, measure it, computing all the metrics.
     *
     * @param mc           class to be measured
     * @param v            version whose measure is referred to
     * @param touchedFiles number of files touched by referring commit
     * @param loader       <code>ObjectLoader</code> object, for technical measures
     * @param df           <code>Diff</code> object, for technical measures
     * @param diff         <code>DiffFormatter</code> object, for technical measures
     * @param addedLOCs    LOCs added in a commit
     * @param removedLOCs  LOCs removed in a commit
     * @param author       author of a single commit
     */
    public void computeMetrics(MeasuredClass mc, Version v, Integer touchedFiles, ObjectLoader loader, DiffFormatter df,
                               DiffEntry diff, Integer addedLOCs, Integer removedLOCs, String author) {
        for (Metric m : this.getMetricsAsList()) {
            m.executeComputation(this, mc, v, touchedFiles, loader, df, diff, addedLOCs, removedLOCs, author);
        }
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("Version", "Name", this.datasetNamesToStringList(), "Buggy");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetAttributes(this.version.getSortedID(), this.name, this.metricsToStringList(), this.isBuggy());
        return this.datasetAttributes;
    }
}
