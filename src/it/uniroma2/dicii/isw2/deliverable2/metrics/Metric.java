package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * A single metric, to be specialized.
 */
public abstract class Metric {
    protected String datasetName;
    protected Float value = Float.valueOf(0);

    protected Metric() {
        this.datasetName = this.initDatasetName();
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    /**
     * Convert metric name to a exportable, human-readble form
     *
     * @return
     */
    protected abstract String initDatasetName();

    /**
     * Convert metric value to a exportable, human-readble form
     *
     * @return
     */
    public abstract String getDatasetValue();

    /**
     * Actively execute the measure of the specified metric.
     * Different with respect to <code>compute</code>, since it is inherited by the specific class,
     * while this method is invoked in a generalized way.
     *
     * @param measure      measure which this metric is part of
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
    public void executeComputation(Measure measure, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                                   DiffEntry diff, Integer[] locs) {
        try {
            this.setValue(this.compute(measure, mc, commit, loader, df, diff, locs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call for metric computation
     *
     * @param measure      measure which this metric is part of
     * @param mc           class to be measured
     * @param v            version whose measure is referred to
     * @param touchedFiles number of files touched by referring commit
     * @param loader       <code>ObjectLoader</code> object, for technical measures
     * @param df           <code>Diff</code> object, for technical measures
     * @param diff         <code>DiffFormatter</code> object, for technical measures
     * @param addedLOCs    LOCs added in a commit
     * @param removedLOCs  LOCs removed in a commit
     * @param author       author of a single commit
     * @throws IOException
     */
    public abstract Float compute(Measure measure, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                                  DiffEntry diff, Integer[] locs) throws IOException;
}
