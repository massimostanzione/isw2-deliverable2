package it.uniroma2.dicii.isw2.deliverable2;

import it.uniroma2.dicii.isw2.deliverable2.entities.Project;
import it.uniroma2.dicii.isw2.deliverable2.enumerations.LabelingMethod;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class, as a "start point" for the program.
 */
public class Main {
    private static final Logger log = LoggerInst.getSingletonInstance();

    public static void main(String[] args) {
        Project p1 = new Project("BOOKKEEPER", "https://github.com/apache/bookkeeper", "release-4.13.0");
        Project p2 = new Project("OPENJPA", "https://github.com/apache/openjpa", "3.2.0");
        List<Project> beingAnalyzed = new ArrayList<>();
        beingAnalyzed.add(p1);
        beingAnalyzed.add(p2);
        for (Integer i = 0; i < beingAnalyzed.size(); i++) {
            try {
                new ProjectAnalyzer(beingAnalyzed.get(i), LabelingMethod.PROPORTION_INCREMENTAL).run();
            } catch (Exception e) {
                log.severe(e.getMessage());
            }
        }
    }
}
