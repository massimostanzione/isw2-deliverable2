package it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * No cost sensitive classifier: simply return nothing different than what was passed.
 */
public class NoCostSensitive implements CostSensitive {
    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        return cl;
    }
}
