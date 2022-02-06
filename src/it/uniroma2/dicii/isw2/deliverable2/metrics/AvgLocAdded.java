package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * Average Lines of code added.
 */
public class AvgLocAdded extends Metric {

    @Override
    protected String initDatasetName() {
        return "Avg_LOC_Added";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value);
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Version v, Integer touchedFiles, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer addedLOCs, Integer removedLOCs, String author)
            throws IOException {
        return m.getLocAdded().getValue() / m.getNr().getValue();
    }
}
