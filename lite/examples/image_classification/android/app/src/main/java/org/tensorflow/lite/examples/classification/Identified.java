package org.tensorflow.lite.examples.classification;

import java.io.Serializable;

public class Identified {

    private String oldDiseaseName;
    private String newDiseaseName;
    private String image;

    public Identified(String oldDiseaseName, String newDiseaseName, String image) {
        this.oldDiseaseName = oldDiseaseName;
        this.newDiseaseName = newDiseaseName;
        this.image = image;
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

    public String getImage() { return image; }
}
