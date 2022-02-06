package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * Sum of added and removed lines of code.
 */
public class LOC_Touched extends Metric {

    @Override
    protected String initDatasetName() {
        return "LOC_Touched";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value.intValue());
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Version v, Integer touchedFiles, ObjectLoader loader, DiffFormatter df, DiffEntry diff,
                         Integer addedLOCs, Integer removedLOCs, String author)
            throws IOException {
        return (float) (addedLOCs + removedLOCs);
    }
}
