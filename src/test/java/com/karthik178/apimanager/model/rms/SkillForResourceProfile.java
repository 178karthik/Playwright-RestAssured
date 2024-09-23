package com.karthik178.apimanager.model.rms;

public class SkillForResourceProfile {

    private String id;
    private String skillType;
    private String name;
    private String skillRating;
    private String minExperience;
    private String priority;
    private String skillGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkillRating() {
        return skillRating;
    }

    public void setSkillRating(String skillRating) {
        this.skillRating = skillRating;
    }

    public String getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(String minExperience) {
        this.minExperience = minExperience;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSkillGroup() {
        return skillGroup;
    }

    public void setSkillGroup(String skillGroup) {
        this.skillGroup = skillGroup;
    }

    @Override
    public String toString() {
        return "SkillForResourceProfile{" +
                "id='" + id + '\'' +
                ", skillType='" + skillType + '\'' +
                ", name='" + name + '\'' +
                ", skillRating='" + skillRating + '\'' +
                ", minExperience='" + minExperience + '\'' +
                ", priority='" + priority + '\'' +
                ", skillGroup='" + skillGroup + '\'' +
                '}';
    }
}
