package it.uniroma2.dicii.isw2.deliverable2.entities;

import java.util.List;

/**
 * Just for inspection.
 * Each entry explains when and by what commit (with lifecycle) a class was touched.
 */
public class TouchedClassesInspection extends ExportableAsDatasetRecord {
    private MeasuredClass mc;
    private Version v;
    private Commit c;
    private Bug b;
    private BugLifecycle bl;

    public MeasuredClass getMc() {
        return mc;
    }

    public void setMc(MeasuredClass mc) {
        this.mc = mc;
    }

    public Version getV() {
        return v;
    }

    public void setV(Version v) {
        this.v = v;
    }

    public Commit getC() {
        return c;
    }

    public void setC(Commit c) {
        this.c = c;
    }

    public Bug getB() {
        return b;
    }

    public void setB(Bug b) {
        this.b = b;
    }

    public BugLifecycle getBl() {
        return bl;
    }

    public void setBl(BugLifecycle bl) {
        this.bl = bl;
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("Class...", "... in version ...", "... is touched by commit ...",
                "... related to bug...", "... whose lifecycle is...");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.mc.getName(), this.v.getSortedID(), this.c.getCommitID(), this.b.getID(), this.bl.toString());
        return this.datasetRecord;
    }
}
