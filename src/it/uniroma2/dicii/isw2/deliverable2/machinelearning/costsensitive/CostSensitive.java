package it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * General interface for Cost Sensitive classifiers.
 */
public interface CostSensitive {
    /**
     * Modify the classifier with the cost sensitive property that specialize the <code>CostSensitive</code> interface.
     *
     * @param cl    classifier to be modified to be cost-sensitive
     * @param insts what istance is training referring to
     * @return the classifier, adequately adapted (filtered) to be cost-sensitive as specified
     * @throws Exception
     */
    Classifier getFilteredClassifier(Classifier cl, Instances insts);
}
