package com.karthik178.apimanager.model.rms;

import java.util.List;

public class Skill {

    private String id;
    private String name;
    private String isStaffingSkill;
    private String skillGroup;
    private List<String> employeePracticeArea;
    private String hrKeySkill;
    private String hrKeySkillId;

    public Skill() {
    }

    public Skill(String id, String name, String isStaffingSkill, String skillGroup, List<String> employeePracticeArea, String hrKeySkill, String hrKeySkillId) {
        this.id = id;
        this.name = name;
        this.isStaffingSkill = isStaffingSkill;
        this.skillGroup = skillGroup;
        this.employeePracticeArea = employeePracticeArea;
        this.hrKeySkill = hrKeySkill;
        this.hrKeySkillId = hrKeySkillId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsStaffingSkill() {
        return isStaffingSkill;
    }

    public void setIsStaffingSkill(String isStaffingSkill) {
        this.isStaffingSkill = isStaffingSkill;
    }

    public String getSkillGroup() {
        return skillGroup;
    }

    public void setSkillGroup(String skillGroup) {
        this.skillGroup = skillGroup;
    }

    public List<String> getEmployeePracticeArea() {
        return employeePracticeArea;
    }

    public void setEmployeePracticeArea(List<String> employeePracticeArea) {
        this.employeePracticeArea = employeePracticeArea;
    }

    public String getHrKeySkill() {
        return hrKeySkill;
    }

    public void setHrKeySkill(String hrKeySkill) {
        this.hrKeySkill = hrKeySkill;
    }

    public String getHrKeySkillId() {
        return hrKeySkillId;
    }

    public void setHrKeySkillId(String hrKeySkillId) {
        this.hrKeySkillId = hrKeySkillId;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isStaffingSkill='" + isStaffingSkill + '\'' +
                ", skillGroup='" + skillGroup + '\'' +
                ", employeePracticeArea=" + employeePracticeArea +
                ", hrKeySkill='" + hrKeySkill + '\'' +
                ", hrKeySkillId='" + hrKeySkillId + '\'' +
                '}';
    }
}
