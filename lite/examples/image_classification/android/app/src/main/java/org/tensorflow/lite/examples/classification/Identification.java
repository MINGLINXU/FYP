package org.tensorflow.lite.examples.classification;

import java.io.Serializable;

public class Identification implements Serializable {

    private String diseaseName;
    private Float percentage;
    private String image;

    public Identification(String diseaseName, Float percentage, String image){
        this.diseaseName = diseaseName;
        this.percentage = percentage;
        this.image = image;
    }


    public String getdiseaseName() {
        return diseaseName;
    }

    public Float getPercentage() { return percentage; }

    public String getImage() { return image; }

}
