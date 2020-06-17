package org.tensorflow.lite.examples.classification;

import java.io.Serializable;

public class identification implements Serializable {

    private String diseaseName;
    private String percentage;

    public identification(String diseaseName, String percentage){
        this.diseaseName = diseaseName;
        this.percentage = percentage;
    }
    

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getPercentage() {
        return percentage;
    }
}
