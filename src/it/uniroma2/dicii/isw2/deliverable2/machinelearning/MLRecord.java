package it.uniroma2.dicii.isw2.deliverable2.machinelearning;

import it.uniroma2.dicii.isw2.deliverable2.entities.ExportableAsDatasetRecord;
import weka.classifiers.Classifier;

import java.util.List;

/**
 * A single record with all the ML information, as main output for this deliverable.
 */
public class MLRecord extends ExportableAsDatasetRecord {
    private String projName;
    private Integer trainNo;
    private Double trainPerc;
    private Double trainDefect;
    private Double testDefect;
    private Classifier classifier;
    private String balancing;
    private String featureSelection;
    private String sensitivity;
    private double[][] confusionMatrix;
    private double TP;
    private double FP;
    private double TN;
    private double FN;
    private Double precision;
    private Double recall;
    private Double AUC;
    private Double kappa;

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public Integer getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(Integer trainNo) {
        this.trainNo = trainNo;
    }

    public Double getTrainPerc() {
        return trainPerc;
    }

    public void setTrainPerc(Double trainPerc) {
        this.trainPerc = trainPerc;
    }

    public Double getTrainDefect() {
        return trainDefect;
    }

    public void setTrainDefect(Double trainDefect) {
        this.trainDefect = trainDefect;
    }

    public Double getTestDefect() {
        return testDefect;
    }

    public void setTestDefect(Double testDefect) {
        this.testDefect = testDefect;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public String getBalancing() {
        return balancing;
    }

    public void setBalancing(String balancing) {
        this.balancing = balancing;
    }

    public String getFeatureSelection() {
        return featureSelection;
    }

    public void setFeatureSelection(String featureSelection) {
        this.featureSelection = featureSelection;
    }

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public double[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(double[][] ds) {
        this.confusionMatrix = ds;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Double getRecall() {
        return recall;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }

    public Double getAUC() {
        return AUC;
    }

    public void setAUC(Double aUC) {
        AUC = aUC;
    }

    public Double getKappa() {
        return kappa;
    }

    public void setKappa(Double kappa) {
        this.kappa = kappa;
    }

    public double getTP() {
        return TP;
    }

    public void setTP(double TP) {
        this.TP = TP;
    }

    public double getFP() {
        return FP;
    }

    public void setFP(double FP) {
        this.FP = FP;
    }

    public double getTN() {
        return TN;
    }

    public void setTN(double TN) {
        this.TN = TN;
    }

    public double getFN() {
        return FN;
    }

    public void setFN(double FN) {
        this.FN = FN;
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("Dataset", "#TrainingRelease", "%training", "%Defective_in_training",
                "%Defective_in_testing", "Classifier", "Feature_Selection", "Balancing", "Sensitivity", "TP", "FP",
                "TN", "FN", "Precision", "Recall", "AUC", "Kappa");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.projName, this.trainNo, this.trainPerc, this.trainDefect,
                this.testDefect, this.classifier.getClass().getSimpleName(), this.featureSelection, this.balancing, this.sensitivity,
                this.TP, this.FP, this.TN, this.FN, this.precision, this.recall, this.AUC,
                this.kappa);
        return this.datasetRecord;
    }

}
