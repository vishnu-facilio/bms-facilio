package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanHazardContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddWorkorderHazardsFromSafetyPlanCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		V3WorkOrderContext workOrder = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(workOrder != null && workOrder.getSafetyPlan() != null && workOrder.getSafetyPlan().getId() > 0 && AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			List<V3SafetyPlanHazardContext> hazards = HazardsAPI.fetchAssociatedHazards(workOrder.getSafetyPlan().getId());
			if(CollectionUtils.isNotEmpty(hazards)) {
				List<V3WorkorderHazardContext> woHazards = new ArrayList<V3WorkorderHazardContext>();
				for(V3SafetyPlanHazardContext sfHazard : hazards) {
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
		return false;
	}

}
