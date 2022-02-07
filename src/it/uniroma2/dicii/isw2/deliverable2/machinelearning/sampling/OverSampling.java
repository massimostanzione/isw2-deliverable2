package it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;

import java.util.logging.Logger;

/**
 * OverSampling technique.
 */
public class OverSampling implements Sampling {
    private static final Logger log = LoggerInst.getSingletonInstance();

    private double computeMajority(Instances insts) {
        Integer trueVals = 0;
        Integer falseVals = 0;
        for (Instance inst : insts) {
            if (inst.stringValue(inst.attribute(inst.numAttributes() - 1)).equals("true"))
                trueVals += 1;
            else
                falseVals += 1;
        }
        return trueVals > falseVals ? ((double) (trueVals * 2) / insts.size()) * 100 : ((double) (falseVals * 2) / insts.size()) * 100;

    }

    @Override
    public Classifier getFilteredClassifier(Classifier cl, Instances insts) {
        FilteredClassifier filteredCl = new FilteredClassifier();
        Resample resample = new Resample();
        try {
            resample.setInputFormat(insts);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        // Equivalent to "-B" option
        resample.setBiasToUniformClass(1.0);
        // Equivalent to "-Z" option
        resample.setSampleSizePercent(2 * computeMajority(insts));
        filteredCl.setClassifier(cl);
        filteredCl.setFilter(resample);
        return filteredCl;
    }
}
