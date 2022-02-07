package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * Sum of added and removed lines of code.
 */
public class LOCTouched extends Metric {

    @Override
    protected String initDatasetName() {
        return "LOC_Touched";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value.intValue());
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer[] locs)
            throws IOException {
        return (float) (locs[0] + locs[1]);
    }
}
