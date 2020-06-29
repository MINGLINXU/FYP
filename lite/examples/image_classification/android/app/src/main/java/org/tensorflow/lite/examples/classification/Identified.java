package org.tensorflow.lite.examples.classification;

public class Identified {

    private String oldDiseaseName;
    private String newDiseaseName;

    public Identified(String oldDiseaseName, String newDiseaseName) {
        this.oldDiseaseName = oldDiseaseName;
        this.newDiseaseName = newDiseaseName;
    }

    public String getOldDiseaseName() {
        return oldDiseaseName;
    }

    public void setOldDiseaseName(String oldDiseaseName) {
        this.oldDiseaseName = oldDiseaseName;
    }

    public String getNewDiseaseName() {
        return newDiseaseName;
    }

    public void setNewDiseaseName(String newDiseaseName) {
        this.newDiseaseName = newDiseaseName;
    }
}
