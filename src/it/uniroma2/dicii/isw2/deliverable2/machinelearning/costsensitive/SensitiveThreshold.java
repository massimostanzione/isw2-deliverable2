package it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive;

import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.core.Instances;

/**
 * <i>SensitiveThreshold</i> cost-sensitive classifier, with a custom, pre-defined cost matrix
 */
public class SensitiveThreshold implements CostSensitive {
    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        CostSensitiveClassifier cscl = new CostSensitiveClassifier();
        cscl.setClassifier(cl);
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, Double.valueOf((double) 0.0));
        costMatrix.setCell(0, 1, Double.valueOf((double) 10.0));
        costMatrix.setCell(1, 0, Double.valueOf((double) 1.0));
        costMatrix.setCell(1, 1, Double.valueOf((double) 0.0));
        cscl.setCostMatrix(costMatrix);
        cscl.setMinimizeExpectedCost(true);
        return cscl;
    }

}
