package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;

/**
 * SMOTE technique.
 */
public class SMOTESampling implements Sampling {

    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) throws Exception {
        FilteredClassifier filteredCl = new FilteredClassifier();
        SMOTE smote = new SMOTE();
        smote.setInputFormat(insts);
        filteredCl.setClassifier(cl);
        filteredCl.setFilter(smote);
        return filteredCl;
    }

}
