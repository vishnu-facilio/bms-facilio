package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.AlarmDefinitionTaggingModule;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.ConfigParams;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AlarmCorrelationRuleAction extends V3Action {

    public String getCriteriaTypeOptions() throws Exception {
        List<Map<String, String>> alarmFilterCriteriaTypeOptions = new ArrayList<>();
        for (AlarmFilterCriteriaType alarmFilterCriteriaType : AlarmFilterCriteriaType.values()) {
            Map<String, String> alarmFilterCriteriaTypeOption = new HashMap<>();
            alarmFilterCriteriaTypeOption.put("label", alarmFilterCriteriaType.getValue());
            alarmFilterCriteriaTypeOption.put("value", alarmFilterCriteriaType.getIndex());
            alarmFilterCriteriaTypeOptions.add(alarmFilterCriteriaTypeOption);
        }
        setData(RemoteMonitorConstants.ALARM_FILTER_CRITERIA_TYPES, alarmFilterCriteriaTypeOptions);
        return SUCCESS;
    }

    public String getControllerTypesAsMap() throws Exception {
        Map<Integer, Object> controllerTypesMap = new HashMap<>();
        for (ControllerType controllerType : ControllerType.values()) {
            Map<String,Object> controllerTypeObj = new HashMap<>();
            controllerTypeObj.put("index", controllerType.getIndex());
            controllerTypeObj.put("value", controllerType.getValue());
            Integer key = controllerType.getIndex();
            controllerTypesMap.put(key, controllerTypeObj);
        }
        setData(RemoteMonitorConstants.CONTROLLER_TYPE_MAP, controllerTypesMap);
        return SUCCESS;
    }

    public String getAlarmTypeMappingList() throws Exception {
        api currentApi = currentApi();
        ConfigParams configParams = new ConfigParams();
        configParams.setSelectableFieldNames(getSelectableFieldNames());
        FacilioContext listContext = V3Util.fetchList(AlarmDefinitionTaggingModule.MODULE_NAME, (currentApi == api.v3), this.getViewName(), this.getFilters(), this.getExcludeParentFilter(), this.getClientCriteria(),
                this.getOrderBy(), this.getOrderType(), this.getSearch(), this.getPage(), this.getPerPage(), this.getWithCount(), null, null,this.getWithoutCustomButtons(),this.getFetchOnlyViewGroupColumn(),this.getQuickFilter(),configParams);
        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);
        this.setData(recordJSON);
        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;
    }
}
