package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * No sampling required: simply return nothing different than what was passed in input.
 */
public class NoSampling implements Sampling {
    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        return cl;
    }
}
