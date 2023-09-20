package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ServiceTaskTemplateContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServicePlanContext servicePlan;
    private String name;
    private String description;
    private String taskCode;
    private WorkTypeContext workType;
    private List<ServiceTaskTemplateSkillsContext> skills;
    private Integer sequence;
    private Boolean isPhotoMandatory;
    private List<ServicePlanItemsContext> servicePlanItems;
    private List<ServicePlanToolsContext> servicePlanTools;
    private List<ServicePlanServicesContext> servicePlanServices;

}
