package com.facilio.bmsconsoleV3.context.SFG20JobPlan;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SFGScheduleContext {
    @Getter
    @Setter
    public static class ScheduleIntro {
        public String Content;
        public String Notes;
    }
    @Getter
    @Setter
    public static class ScheduleTask {
        public String Title;
        public String Action;
        public String Notes;
        public boolean IsHeader;
        public String SkillSet;
        public String FrequencyPeriod;
        public int FrequencyInterval;
        public int Index;
        public String Criticality;
    }


    @Getter
    @Setter
    public static class ScheduleLegislation {
        public String URL;
        public String Title;
    }


    @Getter
    @Setter
    public static class ServiceTiming {
        public List<String> Criticalities;
        public String FrequencyPeriod;
        public int FrequencyInterval;
        public double Minutes;
    }

    public int ScheduleId;
    public String Code;
    public String Title;
    public String CreatedDate;
    public String DateUpdated;
    public String Version;
    public String UnitOfMeasure;
    public List<String> ScheduleGroupPaths;
    public List<String> NrmCodes;
    public ScheduleIntro ScheduleIntro;
    public List<ScheduleTask> ScheduleTasks;
    public List<ScheduleLegislation> ScheduleLegislations;
    public List<String> ScheduleTypes;
    public List<ServiceTiming> ServiceTimings;
    public List<String> Flags;

    public String getLegislationString() {
        return legislationString;
    }

    public void setLegislationString(String legislationString) {
        this.legislationString = legislationString;
    }

    public String legislationString;
}
