package it.uniroma2.dicii.isw2.deliverable2.io;

import it.uniroma2.dicii.isw2.deliverable2.entities.ExportableAsDatasetRecord;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ARFFExporterPrinter extends ExporterPrinter implements Exporter {
    private static final String ARFF_CONST_RELATION = "@RELATION";
    private static final String ARFF_CONST_ATTRIBUTE = "@ATTRIBUTE";
    private static final String ARFF_CONST_DATA = "@DATA";
    private static final String ARFF_CONST_ATTRTYPE_NUMERIC = "NUMERIC";
    private static final String ARFF_CONST_ATTRTYPE_STRING = "STRING";
    private static final String ARFF_CONST_ATTRTYPE_BOOLEAN = "{true,false}";
    private static ARFFExporterPrinter instance;

    private ARFFExporterPrinter() {
        super();
    }

    public static ARFFExporterPrinter getSingletonInstance() {
        if (instance == null)
            instance = new ARFFExporterPrinter();
        return instance;
    }

    public static void export(String relationName, List<List<String>> dataset, String outname) {
        outname = System.getProperty("user.dir") + outname;
        printLog(outname);
        try {
            File file = new File(outname);
            file.getParentFile().mkdirs();
            boolean alreadyExists = file.createNewFile();
            if (alreadyExists)
                log.finer("ARFF target file already exists.");
            fileWriter = new FileWriter(file);
            if (!dataset.isEmpty()) {
                fileWriter.append(generateHeader(relationName, dataset.size(), dataset.get(0).size()));
                fileWriter.append(ARFF_CONST_RELATION + "\t" + relationName + "\n\n" +
                        determineAttrTypes(dataset) + "\n" + ARFF_CONST_DATA + "\n");
                loadAttrs(dataset);
            }
            fileWriter.close();
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    private static void loadAttrs(List<List<String>> dataset) throws IOException {
        Integer j = -1;
        Integer l = -1;
        Integer recordDim = dataset.get(0).size();
        for (List<String> datasetRecord : dataset) {
            j++;
            l = -1;
            for (String value : datasetRecord) {
                l++;
                if (j > 0) {
                    // Skip attributes (first row)
                    fileWriter.append(value);
                    fileWriter.append(l + 1 < recordDim ? "," : "\n");
                }
            }
        }
    }

    private static String determineAttrTypes(List<List<String>> dataset) {
        StringBuilder sb = new StringBuilder();
        Integer i = -1;
        for (String attribute : dataset.get(0)) {
            i++;
            String attrType = NumberUtils.isParsable(dataset.get(1).get(i)) ? ARFF_CONST_ATTRTYPE_NUMERIC
                    : ARFF_CONST_ATTRTYPE_STRING;
            if (i == dataset.get(0).size() - 1) {
                attrType = ARFF_CONST_ATTRTYPE_BOOLEAN;
            }
            sb.append(ARFF_CONST_ATTRIBUTE).append("\t").append(attribute).append("\t").append(attrType).append("\n");
        }
        return sb.toString();
    }

    /**
     * Adapt dataset to a standardized dataset that can be arranged as ARFF file.
     *
     * @param objList dataset to be adapted. Must inherit <code>ExportableAsDatasetRecord</code>
     * @return adapted dataset
     * @see ExportableAsDatasetRecord
     */
    public List<List<String>> convertToARFFExportable(List<?> objList) {
        return convertToSpecificExportable(objList);
    }

    /**
     * Adapt a dataset and export it to ARFF in a standardized way.
     *
     * @param objList dataset to be adapted and exported
     * @param path    output file name
     */
    public void convertAndExport(String relationName, List<?> objList, String path) {
        export(relationName, convertToARFFExportable(objList), path);
    }

    /**
     * Generate ARFF header.
     * Based on <a href=https://www.cs.waikato.ac.nz/ml/weka/arff.html>official Weka documentation</a>.
     *
     * @param relationName relation name
     * @param records      number of records
     * @param attributes   number of attributes
     * @return a fully-formatted ARFF header
     */
    private static String generateHeader(String relationName, Integer records, Integer attributes) {
        return "% 1. Title: " + relationName + " project analysis\n" + "% \n"
                + "% 2. Sources: \n"
                + "%    (a) Massimo Stanzione\n"
                + "%    (b) " + relationName + " public git repository\n"
                + "% \n" + "% 3. Relevant Information:\n"
                + "%      This dataset is used in the deliverable 2 of the \"Ingegneria del Software II\" exam,\n"
                + "%      Machine Learning module, Faculty of Engineering, University of Rome Tor Vergata.\n" + "% \n"
                + "% 4. Number of Instances: " + records + "\n" + "% \n"
                + "% 5. Number of Attributes: " + attributes + "\n"
                + "%\n\n";
    }

    @Override
    public List<List<String>> convertToSpecificExportable(List<?> objList) {
        List<List<String>> ret = new ArrayList<>();
        if (!objList.isEmpty()) {
            ret = Stream
                    .concat(ret.stream(), ((ExportableAsDatasetRecord) objList.get(0)).getDatasetAttributes().stream())
                    .collect(Collectors.toList());
            for (Object obj : objList) {
                ret = Stream.concat(ret.stream(), ((ExportableAsDatasetRecord) obj).getDatasetRecord().stream())
                        .collect(Collectors.toList());
            }
        }
        return ret;
    }
}
