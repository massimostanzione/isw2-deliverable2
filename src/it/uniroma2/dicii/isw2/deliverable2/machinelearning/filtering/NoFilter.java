package it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering;

import weka.filters.Filter;

/**
 * No feature selecion: simply return nothing.
 */
public class NoFilter implements FeatureSelectionMethod {
    @Override
    public Filter getFSFilter() {
        return null;
    }
}
