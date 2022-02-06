package it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive;

import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.core.Instances;

/**
 * <i>SensitiveLearning</i> cost-sensitive classifier, with a custom, pre-defined cost matrix
 */
public class SensitiveLearning implements CostSensitive {
    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        CostSensitiveClassifier cscl = new CostSensitiveClassifier();
        cscl.setClassifier(cl);
        CostMatrix cm = new CostMatrix(2);
        cm.setCell(0, 0, Double.valueOf(0.0));
        cm.setCell(0, 1, Double.valueOf(10.0));
        cm.setCell(1, 0, Double.valueOf(1.0));
        cm.setCell(1, 1, Double.valueOf(0.0));
        cscl.setCostMatrix(cm);
        cscl.setMinimizeExpectedCost(false);
        return cscl;
    }

}
