package com.karthik178.apimanager.model.deletemodel;


public class SkillGroupWithPractice {
    private String hierarchyElementId;
    private String clientId;
    private int disable;
    private String name;
    private String priority;
    private String type;

    public String getHierarchyElementId() {
        return hierarchyElementId;
    }

    public void setHierarchyElementId(String hierarchyElementId) {
        this.hierarchyElementId = hierarchyElementId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getDisable() {
        return disable;
    }

    public void setDisable(int disable) {
        this.disable = disable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SkillGroupWithPractice(String hierarchyElementId, String clientId, int disable, String name, String priority, String type) {
        this.hierarchyElementId = hierarchyElementId;
        this.clientId = clientId;
        this.disable = disable;
        this.name = name;
        this.priority = priority;
        this.type = type;
    }
}
