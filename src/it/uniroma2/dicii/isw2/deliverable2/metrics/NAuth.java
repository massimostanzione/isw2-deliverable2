package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Commit;
import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Number of authors involved in a class per version, updated at each commit.
 */
public class NAuth extends Metric {
    List<String> authors = new ArrayList<>();

    @Override
    protected String initDatasetName() {
        return "NAuth";
    }

    @Override
    public String getDatasetValue() {
        return String.valueOf(this.value.intValue());
    }

    @Override
    public Float compute(Measure m, MeasuredClass mc, Commit commit, ObjectLoader loader, DiffFormatter df,
                         DiffEntry diff, Integer[] locs)
            throws IOException {
        String author = commit.getReferredRawCommit().getAuthorIdent().getName();
        if (!this.authors.contains(author)) {
            this.authors.add(author);
        }
        return (float) (this.authors.size());
    }

}
