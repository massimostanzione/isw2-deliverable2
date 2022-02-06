package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for automatic dataset creation: each specializing class must override attributes and
 * records methods, in such a way their data can be exported in CSV - or any other way - in a
 * common, standardized way.
 */
public abstract class ExportableAsDatasetRecord {
    protected List<List<String>> datasetAttributes;
    protected List<List<String>> datasetRecord;

    public abstract List<List<String>> getDatasetAttributes();

    protected void setDatasetAttributes(Object... attributes) {
        this.datasetAttributes = convert(attributes);
    }

    public abstract List<List<String>> getDatasetRecord();

    protected void setDatasetRecord(Object... datasetRecord) {
        this.datasetRecord = convert(datasetRecord);
    }

    /**
     * Convert a sequence of items (data) in such a way it can be standardized.
     *
     * @param array data
     * @return standardized data
     */
    private List<List<String>> convert(Object... array) {
        List<List<String>> title = new ArrayList<>();
        List<String> titleRecord = new ArrayList<>();
        for (Object o : array) {
            if (o instanceof List) {
                for (Object iterated : (List<?>) o) {
                    titleRecord.add(String.valueOf(iterated));
                }
            } else {
                titleRecord.add(String.valueOf(o));
            }
        }
        title.add(titleRecord);
        return title;
    }
}
