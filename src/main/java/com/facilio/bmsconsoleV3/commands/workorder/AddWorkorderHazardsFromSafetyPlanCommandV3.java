package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanHazardContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddWorkorderHazardsFromSafetyPlanCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams =  Constants.getBodyParams(context);
        if(bodyParams != null) {
            Boolean isFetchSafetyPlanHazardPrecaution = (Boolean) bodyParams.get("addSafetyPlanHazardPrecaution");
            List<V3WorkOrderContext> wos = recordMap.get(moduleName);

            if (CollectionUtils.isNotEmpty(wos)) {

                V3WorkOrderContext workOrder = wos.get(0);

                if (isFetchSafetyPlanHazardPrecaution != null && isFetchSafetyPlanHazardPrecaution && workOrder.getSafetyPlan() != null && workOrder.getSafetyPlan().getId() > 0 && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                    List<V3SafetyPlanHazardContext> hazards = HazardsAPI.fetchAssociatedHazards(workOrder.getSafetyPlan().getId());
                    if (CollectionUtils.isNotEmpty(hazards)) {
                        List<V3WorkorderHazardContext> woHazards = new ArrayList<V3WorkorderHazardContext>();
                        for (V3SafetyPlanHazardContext sfHazard : hazards) {
                            Map<Long, V3WorkorderHazardContext> props = checkIfHazardAvailable(workOrder,sfHazard);
                            if(props != null && props.size() == 0) {
                                V3WorkorderHazardContext temp = new V3WorkorderHazardContext();
                                temp.setWorkorder(workOrder);
                                temp.setHazard(sfHazard.getHazard());
                                woHazards.add(temp);
                            }
                        }
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD);
                        List<FacilioField> fields = modBean.getAllFields(module.getName());
                        V3RecordAPI.addRecord(false, woHazards, module, fields);
                    }
                }else{
                    Criteria criteria = new Criteria();
                    Condition condition = CriteriaAPI.getCondition("WORKORDER_ID", "workorder", String.valueOf(workOrder.getId()), NumberOperators.EQUALS);
                    criteria.addAndCondition(condition);
                    V3RecordAPI.deleteRecords(FacilioConstants.ContextNames.WORKORDER_HAZARD,criteria,false);
                }
            }
        }
        return false;
    }
    public static Map<Long, V3WorkorderHazardContext> checkIfHazardAvailable(V3WorkOrderContext workOrder, V3SafetyPlanHazardContext sfHazard) throws Exception {
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition("WORKORDER_ID", "workorder", String.valueOf(workOrder.getId()), NumberOperators.EQUALS);
        Condition condition_1 = CriteriaAPI.getCondition("HAZARD_ID", "hazard", String.valueOf(sfHazard.getHazard().getId()), NumberOperators.EQUALS);
        criteria.addAndCondition(condition);
        criteria.addAndCondition(condition_1);
        return V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORKORDER_HAZARD, null, V3WorkorderHazardContext.class, criteria, null);
    }
}
