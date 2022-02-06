package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * General interface for sampling techniques.
 */
public interface Sampling {
    /**
     * Modify the classifier with the sampling technique that specialize the <code>Sampling</code> interface.
     *
     * @param cl    classifier to be modified
     * @param insts what istance is training referring to
     * @return the classifier, adequately adapted (filtered) to the specified technique
     * @throws Exception
     */
    Classifier getFilteredClassifier(Classifier cl, Instances insts) throws Exception;
}
