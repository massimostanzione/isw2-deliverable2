package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;

/**
 * Number of touched files in a commit.
 */
public class ChangesetSize extends Metric {

    @Override
    protected String initDatasetName() {
        return "ChgSetSize";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value.intValue());
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer[] locs)
            throws IOException {
        return Float.valueOf(commit.getTouchedFiles().size());
    }

}
