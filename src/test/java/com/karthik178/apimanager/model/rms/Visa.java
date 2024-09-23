package com.karthik178.apimanager.model.rms;

import java.util.ArrayList;
import java.util.List;

public class Visa {

    private List<String> visaType;
    private String isRequired;
    private String visaCountry;
    private String visaRequirements;

    public Visa(List<String> visaType, String isRequired, String visaCountry, String visaRequirements) {
        this.visaType = visaType;
        this.isRequired = isRequired;
        this.visaCountry = visaCountry;
        this.visaRequirements = visaRequirements;
    }
    public Visa(String isRequired) {
        if (isRequired == "false") {
            this.visaType = new ArrayList<>();
            this.isRequired = "false";
            this.visaCountry = "";
            this.visaRequirements = "";
        }

    }

    public Visa() {
    }

    public List<String> getVisaType() {
        return visaType;
    }

    public void setVisaType(List<String> visaType) {
        this.visaType = visaType;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public String getVisaCountry() {
        return visaCountry;
    }

    public void setVisaCountry(String visaCountry) {
        this.visaCountry = visaCountry;
    }

    public String getVisaRequirements() {
        return visaRequirements;
    }

    public void setVisaRequirements(String visaRequirements) {
        this.visaRequirements = visaRequirements;
    }

    @Override
    public String toString() {
        return "Visa{" +
                "visaType=" + visaType +
                ", isRequired='" + isRequired + '\'' +
                ", visaCountry='" + visaCountry + '\'' +
                ", visaRequirements='" + visaRequirements + '\'' +
                '}';
    }
}
