package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
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
    public Float compute(Measure m, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer[] locs)
            throws IOException {
        return m.getLocAdded().getValue() / m.getNr().getValue();
    }
}
