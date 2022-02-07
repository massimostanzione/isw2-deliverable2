package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.logging.Logger;

/**
 * UnderSampling technique
 */
public class UnderSampling implements Sampling {
    private static final Logger log = LoggerInst.getSingletonInstance();

    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        FilteredClassifier filteredCl = new FilteredClassifier();
        SpreadSubsample spreadSubsample = new SpreadSubsample();
        // Equivalent to "-M" option
        spreadSubsample.setDistributionSpread(1.0);
        try {
            spreadSubsample.setInputFormat(insts);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        filteredCl.setClassifier(cl);
        filteredCl.setFilter(spreadSubsample);
        return filteredCl;
    }
}
