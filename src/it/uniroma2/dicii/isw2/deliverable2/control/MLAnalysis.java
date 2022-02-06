package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.Project;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import it.uniroma2.dicii.isw2.deliverable2.io.CSVExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.MLRecord;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive.CostSensitive;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive.NoCostSensitive;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive.SensitiveLearning;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.costsensitive.SensitiveThreshold;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering.BestFirstFilter;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering.FeatureSelectionMethod;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.filtering.NoFilter;
import it.uniroma2.dicii.isw2.deliverable2.machinelearning.sampling.*;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * ML analysis control class.
 */
public class MLAnalysis {
    private static Logger log = LoggerInst.getSingletonInstance();

    /**
     * Perform ML analysis via Weka API.
     *
     * @throws Exception
     */
    public static void performWekaMLAnalysis(Project p) throws Exception {
        log.info(() -> "Running Weka ML analysis. It may take a while, please wait...");
        String projName = p.getName();
        List<Version> versionList = p.getVersionList();
        List<MLRecord> mlAnalysis = new ArrayList<>();

        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new RandomForest());
        classifiers.add(new NaiveBayes());
        classifiers.add(new IBk());

        List<FeatureSelectionMethod> featureSelectionMethods = new ArrayList<>();
        featureSelectionMethods.add(new NoFilter());
        featureSelectionMethods.add(new BestFirstFilter());

        List<Sampling> samplingMethods = new ArrayList<>();
        samplingMethods.add(new NoSampling());
        samplingMethods.add(new UnderSampling());
        samplingMethods.add(new OverSampling());
        samplingMethods.add(new SMOTESampling());

        List<CostSensitive> costSensitiveMethods = new ArrayList<>();
        costSensitiveMethods.add(new NoCostSensitive());
        costSensitiveMethods.add(new SensitiveThreshold());
        costSensitiveMethods.add(new SensitiveLearning());
        Instances trainInst = null, testInst = null;
        log.fine(() -> "Version\tFeatureSel\t\t\tCostSensitive\t\t\tClassifier");
        // Walk forward
        for (int index = 1; index < (versionList.size() / 2); index++) {
            ConverterUtils.DataSource trainingDS, testingDS;
            trainingDS = new ConverterUtils.DataSource("./output/" + projName + "/dataset/training/TR" + (index) + ".arff");
            trainInst = trainingDS.getDataSet();
            testingDS = new ConverterUtils.DataSource("./output/" + projName + "/dataset/testing/TE" + (index + 1) + ".arff");
            testInst = testingDS.getDataSet();

            // Perform analysis
            for (Classifier cl : classifiers) {
                for (FeatureSelectionMethod fs : featureSelectionMethods) {
                    for (Sampling sam : samplingMethods) {
                        for (CostSensitive cs : costSensitiveMethods) {
                            int finalIndex = index;
                            log.fine(() -> versionList.get(finalIndex - 1).getName() + "\t" + fs.getClass().getSimpleName() + "\t\t\t" + cs.getClass().getSimpleName() + "\t\t\t" + cl.getClass().getSimpleName());

                            // Convert string attributes to "Nom.",
                            // as to be parsed by Weka, like in GUI
                            StringToNominal stringtoNominal = new StringToNominal();
                            stringtoNominal.setAttributeRange("1-2");
                            stringtoNominal.setInputFormat(testInst);

                            trainInst = Filter.useFilter(trainInst, stringtoNominal);
                            testInst = Filter.useFilter(testInst, stringtoNominal);

                            Filter FSFilter = fs.getFSFilter();
                            if (FSFilter != null) {
                                FSFilter.setInputFormat(trainInst);
                                testInst = Filter.useFilter(testInst, FSFilter);
                                trainInst = Filter.useFilter(trainInst, FSFilter);
                            }
                            testInst.setClassIndex(testInst.numAttributes() - 1);
                            trainInst.setClassIndex(trainInst.numAttributes() - 1);

                            Classifier finalClassifier = cs.getFilteredClassifier(sam.getFilteredClassifier(cl, trainInst), trainInst);

                            finalClassifier.buildClassifier(trainInst);

                            Evaluation eval = null;
                            eval = new Evaluation(testInst);
                            eval.evaluateModel(finalClassifier, testInst);

                            Double trainDefectivePrc = determinePrc("./output/" + projName + "/dataset/training/TR" + (index + 1) + ".csv",
                                    "./output/" + projName + "/dataset/dataset.csv");
                            Double testDefectivePrc = determinePrc("./output/" + projName + "/dataset/testing/TE" + (index + 1) + ".csv",
                                    "./output/" + projName + "/dataset/dataset.csv");
                            double trainPrc;
                            Path trainPath = Paths.get("./output/" + projName + "/dataset/training/TR" + (index) + ".csv");
                            Path datasetPath = Paths.get("./output/" + projName + "/dataset/dataset.csv");
                            Stream<String> str = null;
                            try {
                                str = Files.lines(trainPath);
                            } finally {
                                str.close();
                            }
                            List<String> listDS = Files.readAllLines(datasetPath);
                            trainPrc = ((double) 100 * str.count() - 1) / (listDS.size() - 1);

                            MLRecord ml = new MLRecord();

                            ml.setProjName(projName);
                            ml.setTrainNo(index);
                            ml.setTrainPerc(trainPrc);
                            ml.setTrainDefect(trainDefectivePrc);
                            ml.setTestDefect(testDefectivePrc);

                            ml.setClassifier(cl);
                            ml.setFeatureSelection(fs.getClass().getSimpleName());
                            ml.setBalancing(sam.getClass().getSimpleName());
                            ml.setSensitivity(cs.getClass().getSimpleName());

                            ml.setConfusionMatrix(eval.confusionMatrix());
                            ml.setTP(eval.numTruePositives(0));
                            ml.setFP(eval.numFalsePositives(0));
                            ml.setTN(eval.numTrueNegatives(0));
                            ml.setFN(eval.numFalseNegatives(0));

                            ml.setPrecision(eval.precision(0));
                            ml.setRecall(eval.recall(0));
                            ml.setAUC(eval.areaUnderROC(0));
                            ml.setKappa(eval.kappa());
                            mlAnalysis.add(ml);
                        }
                    }
                }
            }
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(mlAnalysis, "/output/" + projName + "/machinelearning/final.csv");
        log.info(() -> "- Weka ML analysis terminated.");
    }

    /**
     * Utility class, to determine percent of a minority class (e.g. buggy=true) over the whole dataset file
     *
     * @param minClass minority class file
     * @param total    total dataset file
     * @return % of minority class into the dataset
     */
    private static Double determinePrc(String minClass, String total) {
        List<String> minClassLines = new ArrayList<>();
        List<String> totLines = new ArrayList<>();
        try {
            minClassLines = Files.readAllLines(Paths.get(minClass));
            totLines = Files.readAllLines(Paths.get(total));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer trueCnt = 0;
        Integer totLinesCnt = totLines.size() - 1;
        for (var i = 0; i < minClassLines.size(); i++) {
            if (minClassLines.get(i).contains("true"))
                trueCnt += 1;
        }
        return Double.valueOf(100 * trueCnt / (double) totLinesCnt);
    }
}
