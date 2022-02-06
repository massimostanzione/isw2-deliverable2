package it.uniroma2.dicii.isw2.deliverable2.metrics;

import it.uniroma2.dicii.isw2.deliverable2.entities.Measure;
import it.uniroma2.dicii.isw2.deliverable2.entities.MeasuredClass;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectLoader;

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
    public Float compute(Measure measure, MeasuredClass mc, Version v, Integer touchedFiles, ObjectLoader file, DiffFormatter df, DiffEntry diff, Integer addedLOCs, Integer removedLOCs, String author) {
        String content = new String(file.getBytes());
        return (float) (content.lines().count());
    }


}
