package it.uniroma2.dicii.isw2.deliverable2.io;


import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;

import java.io.FileWriter;
import java.util.logging.Logger;

/**
 * Realization of <code>Exporter</code>. Common base class for all the specializations.
 * This is more useful in deliverable II, since there are both CSV and ARFF formats to be managed.
 */
public abstract class ExporterPrinter implements Exporter {
    protected static FileWriter fileWriter;
    protected static Logger log = LoggerInst.getSingletonInstance();

    protected ExporterPrinter() {
        // To avoid instantiation
    }

    protected static void printLog(String outname) {
        log.fine(() -> "- Exporting dataset to " + outname + ".");
    }
}
