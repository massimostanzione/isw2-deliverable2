package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.SpreadSubsample;

/**
 * UnderSampling technique
 */
public class UnderSampling implements Sampling {

    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        FilteredClassifier filteredCl = new FilteredClassifier();
        SpreadSubsample spreadSubsample = new SpreadSubsample();
        // Equivalent to "-M" option
        spreadSubsample.setDistributionSpread(1.0);
        try {
            spreadSubsample.setInputFormat(insts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        filteredCl.setClassifier(cl);
        filteredCl.setFilter(spreadSubsample);
        return filteredCl;
    }
}
