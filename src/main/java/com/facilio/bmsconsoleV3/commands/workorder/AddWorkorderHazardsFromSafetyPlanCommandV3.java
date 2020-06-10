package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.SafetyPlanHazardContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderHazardContext;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.constants.FacilioConstants;
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
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)) {

            V3WorkOrderContext workOrder = wos.get(0);

            if(workOrder.getSafetyPlan() != null && workOrder.getSafetyPlan().getId() > 0 && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                List<SafetyPlanHazardContext> hazards = HazardsAPI.fetchAssociatedHazards(workOrder.getSafetyPlan().getId());
                if (CollectionUtils.isNotEmpty(hazards)) {
                    List<V3WorkorderHazardContext> woHazards = new ArrayList<V3WorkorderHazardContext>();
                    for (SafetyPlanHazardContext sfHazard : hazards) {
                        V3WorkorderHazardContext temp = new V3WorkorderHazardContext();
                        temp.setWorkorder(workOrder);
                        temp.setHazard(sfHazard.getHazard());
                        woHazards.add(temp);
                    }
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD);
                    List<FacilioField> fields = modBean.getAllFields(module.getName());

                    RecordAPI.addRecord(false, woHazards, module, fields);
                }
            }
        }
        return false;
    }
}
