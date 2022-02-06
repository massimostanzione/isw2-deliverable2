package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * A measured Java class, with references to its measures
 */
public class MeasuredClass {
    private String name;
    private List<Measure> measures = new ArrayList<>();

    public MeasuredClass(String path) {
        this.name = path;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the measure of the class at a specific version, if it exists.
     *
     * @param v version
     * @return this class, measured at version <code>v</code>, or null if still not measured
     */
    public Measure atVersion(Version v) {
        for (Measure m : this.measures) {
            if (m.getVersion().getName().equals(v.getName())) {
                return m;
            }
        }
        return null;
    }
}
