package com.facilio.bmsconsoleV3.context.SFG20JobPlan;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SFG20Facilio implements Serializable {
    private static final long serialVersionUID = 1L;
    public Map<String, String> getSfgData() {
        if(sfgData.isEmpty())
        {
            sfgData.put("ScheduleId","scheduleId");
            sfgData.put("Code","code");
            sfgData.put("Title","title");
            sfgData.put("Version","version");
//            sfgData.put("ScheduleGroupPaths","groupPath");
        }
        return sfgData;
    }

    public void setSfgData(Map<String, String> sfgData) {
        this.sfgData = sfgData;
    }

    private Map<String,String> sfgData = new HashMap<>();

    public Map<String, String> getSfgJobPlan() {
        if(sfgJobPlan.isEmpty())
        {
            sfgJobPlan.put("ScheduleId","scheduleId");
            sfgJobPlan.put("Code","sfgCode");
            sfgJobPlan.put("Version","sfgVersion");
        }
        return sfgJobPlan;
    }

    public void setSfgJobPlan(Map<String, String> sfgJobPlan) {
        this.sfgJobPlan = sfgJobPlan;
    }

    private Map<String,String> sfgJobPlan = new HashMap<>();


    public Map<String, String> getSfgJobPlanSection() {
        if(sfgJobPlanSection.isEmpty())
        {
            sfgJobPlanSection.put("ScheduleId","scheduleId");
            sfgJobPlanSection.put("Title","name");
            sfgJobPlanSection.put("Index","sectionCode");
        }
        return sfgJobPlanSection;
    }

    public void setSfgJobPlanSection(Map<String, String> sfgJobPlanSection) {
        this.sfgJobPlanSection = sfgJobPlanSection;
    }

    private Map<String,String> sfgJobPlanSection = new HashMap<>();


    public Map<String, String> getSfgJobPlanIntro() {
        if(sfgJobPlanIntro.isEmpty())
        {
            sfgJobPlanIntro.put("Content","content");
            sfgJobPlanIntro.put("Notes","notes");
            sfgJobPlanIntro.put("HTMLContent","content");
            sfgJobPlanIntro.put("HTMLNote","notes");
        }
        return sfgJobPlanIntro;
    }

    public void setSfgJobPlanIntro(Map<String, String> sfgJobPlanIntro) {
        this.sfgJobPlanIntro = sfgJobPlanIntro;
    }

    private Map<String,String> sfgJobPlanIntro = new HashMap<>();


    public Map<String, String> getSfgJobPlanTask() {
        if(sfgJobPlanTask.isEmpty())
        {
            sfgJobPlanTask.put("Title","subject");
            sfgJobPlanTask.put("Action","actionContent");
            sfgJobPlanTask.put("Notes","notes");
            sfgJobPlanTask.put("Index","notes");
            sfgJobPlanTask.put("SkillSet","skillSet");
        }
        return sfgJobPlanTask;
    }

    public void setSfgJobPlanTask(Map<String, String> sfgJobPlanTask) {
        this.sfgJobPlanTask = sfgJobPlanTask;
    }

    private Map<String,String> sfgJobPlanTask = new HashMap<>();



}
