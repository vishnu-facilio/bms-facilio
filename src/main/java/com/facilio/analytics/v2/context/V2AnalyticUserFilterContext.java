package com.facilio.analytics.v2.context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2AnalyticUserFilterContext {

    private long id=-1;
    private long reportId=-1;
    private Long fieldId;
    private String special_field_name;
    private String default_values;
    private String moduleName;

}
