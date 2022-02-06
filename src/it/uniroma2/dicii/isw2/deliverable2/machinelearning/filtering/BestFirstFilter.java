package it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

/**
 * Feature Selection: BestFirst method.
 */
public class BestFirstFilter implements FeatureSelectionMethod {
    @Override
    public Filter getFSFilter() {
        AttributeSelection filter = new AttributeSelection();
        CfsSubsetEval evalFs = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        filter.setEvaluator(evalFs);
        filter.setSearch(search);
        return filter;
    }
}
