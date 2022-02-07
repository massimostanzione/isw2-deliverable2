package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * Total amount of lines of code in a class.
 */
public class Size extends Metric {

    @Override
    protected String initDatasetName() {
        return "Size";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value.intValue());
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer[] locs)
            throws IOException {
        String content = new String(loader.getBytes());
        return (float) (content.lines().count());
    }
}
