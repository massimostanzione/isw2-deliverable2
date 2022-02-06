package it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering;

import weka.filters.Filter;

/**
 * General interface for Feature Selection methods.
 */
public interface FeatureSelectionMethod {
    /**
     * Get the specified Feature Selection filter, with class that specialize the <code>FeatureSelectionMethod</code> interface.
     *
     * @return requested filter
     */
    Filter getFSFilter();
}
