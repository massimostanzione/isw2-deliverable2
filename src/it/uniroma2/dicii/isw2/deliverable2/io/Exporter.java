package it.uniroma2.dicii.isw2.deliverable2.io;

import java.util.List;

/**
 * A Exporter, that can be specialized to export dataset in a specific standardized way.
 */
public interface Exporter {
    static Exporter getSingletonInstance() {
        return null;
    }

    /**
     * Export dataset to a file file
     *
     * @param dataset dataset to be exported
     * @param outname output file
     */
    static void export(List<List<String>> dataset, String outname) {

    }

    /**
     * Adapt dataset to a standardized dataset that can be arranged in a specific way.
     *
     * @param objList dataset to be adapted.
     * @return adapted dataset
     */
    List<List<String>> convertToSpecificExportable(List<?> objList);

    /**
     * Adapt a dataset and export it to a specific format in a standardized way.
     *
     * @param objList dataset to be adapted and exported
     * @param path    output file name
     */
    static void convertAndExport(List<?> objList, String path) {
    }
}
