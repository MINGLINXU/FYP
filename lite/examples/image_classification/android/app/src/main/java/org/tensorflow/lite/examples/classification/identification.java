package org.tensorflow.lite.examples.classification;

import java.io.Serializable;

public class identification implements Serializable {

    private int id;
    private String diseaseName;
    private String percentage;

    public identification(int id, String diseaseName, String percentage){
        this.id = id;
        this.diseaseName = diseaseName;
        this.percentage = percentage;
    }

    public int getId() {
        return id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getPercentage() {
        return percentage;
    }
}
