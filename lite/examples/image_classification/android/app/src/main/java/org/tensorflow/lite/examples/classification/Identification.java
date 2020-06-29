package org.tensorflow.lite.examples.classification;

import java.io.Serializable;

public class Identification implements Serializable {

    private String diseaseName;
    private Float percentage;

    public Identification(String diseaseName, Float percentage){
        this.diseaseName = diseaseName;
        this.percentage = percentage;
    }


    public String getdiseaseName() {
        return diseaseName;
    }

    public Float getPercentage() { return percentage; }

}
