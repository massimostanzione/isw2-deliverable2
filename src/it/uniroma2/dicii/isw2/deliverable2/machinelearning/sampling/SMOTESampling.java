package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;

import java.util.logging.Logger;

/**
 * SMOTE technique.
 */
public class SMOTESampling implements Sampling {
    private static final Logger log = LoggerInst.getSingletonInstance();

    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        FilteredClassifier filteredCl = new FilteredClassifier();
        SMOTE smote = new SMOTE();
        try {
            smote.setInputFormat(insts);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        filteredCl.setClassifier(cl);
        filteredCl.setFilter(smote);
        return filteredCl;
    }

}
