package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.scriptengine.util.ScriptUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ScriptNameSpace(nameSpace = FacilioConstants.ContextNames.PLANNEDMAINTENANCE)
public class FacilioPlannedMaintenanceFunctions {
    public Object updatePPMDetails(Map<String, Object> globalParam, Object... objects) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        if(objects.length != 2){
            throw new IllegalArgumentException("Provide correct inputs");
        }
        Criteria criteria = (Criteria) objects[0];
        HashMap map = (HashMap) objects[1];
        ScriptUtil.fillCriteriaField(criteria, plannedMaintenanceModule.getName());
        List<String> fieldList = (List<String>) map.keySet().stream().collect(Collectors.toList());
        HashMap<String,Object> updateMap = new HashMap<>();
        List<String> fieldsCannotBeUpdated = new ArrayList<>();
        for(String key : fieldList){
            FacilioField field = modBean.getField(key, plannedMaintenanceModule.getName());
            if(field == null){
                continue;
            }
            else if(!field.getModule().getName().equals(plannedMaintenanceModule.getName())){
                continue;
            }
            else if((field.getName().equals("name") && field.getDefault()) || !field.getDefault()){
                updateMap.put(key,map.get(key));
            }
        }

        UpdateRecordBuilder<PlannedMaintenance> updateRecordBuilder = new UpdateRecordBuilder<PlannedMaintenance>()
                .module(plannedMaintenanceModule)
                .fields(modBean.getAllFields(plannedMaintenanceModule.getName()))
                .andCriteria(criteria);
        int reountCount = updateRecordBuilder.updateViaMap(updateMap);

        return "Updated Field - " +updateMap.keySet();
    }
}
