package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;

public class ConstructCriteriaAndSetModuleNameForActivity extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ConstructCriteriaAndSetModuleNameForActivity.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		Long occurrenceId = (Long) context.get(FacilioConstants.ContextNames.ALARM_OCCURRENCE_ID);
		if ((parentId != null && parentId != -1) || (occurrenceId != null && occurrenceId != -1)) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			FacilioModule activityModule = null;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (StringUtils.isEmpty(moduleName)) {
				String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
				List<FacilioModule> subModules = modBean.getSubModules(parentModuleName, ModuleType.ACTIVITY);
				
				if (CollectionUtils.isEmpty(subModules)) {
					LOGGER.info("No Activity Module for module : "+parentModuleName+" and so not adding activities for that");
					return true;
				}
				
				activityModule = subModules.get(0);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, activityModule.getName());
			}
			else {
				activityModule = modBean.getModule(moduleName);
			}
			List<FacilioField> fields = modBean.getAllFields(activityModule.getName());
			int index = -1;
			
			Criteria filterCriteria = new Criteria();
			if (AccountUtil.getCurrentUser().isPortalUser())  {
				for (int i = 0; i<fields.size(); i++) {
				if (fields.get(i).getName().contains("type")) { 
					index = i;
				}
				}
			Condition con = new Condition();
			if( index > -1) {
				con.setField(fields.get(index));
				con.setOperator(StringOperators.IS);
				con.setValue(WorkOrderActivityType.UPDATE_STATUS.getValue() + "," + WorkOrderActivityType.UPDATE.getValue() + "," + WorkOrderActivityType.ADD_COMMENT.getValue() + "," + WorkOrderActivityType.ASSIGN.getValue() + "," + WorkOrderActivityType.APPROVED.getValue() + "," + WorkOrderActivityType.ADD.getValue() + "," + WorkOrderActivityType.VENDOR_ASSIGNED.getValue());
				filterCriteria.addAndCondition(con);
				}

			}
			if (parentId != null && parentId != -1) {
				filterCriteria.addAndCondition(
						CriteriaAPI.getCondition(modBean.getField("parentId", activityModule.getName()),
								String.valueOf(parentId), PickListOperators.IS));
			}
			if (occurrenceId != null && occurrenceId != -1) {
				filterCriteria.addAndCondition(
						CriteriaAPI.getCondition(modBean.getField("occurrenceId", activityModule.getName()),
								String.valueOf(occurrenceId), PickListOperators.IS));
			}

			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
			context.put(FacilioConstants.ContextNames.SORTING_QUERY,
					modBean.getField("ttime", activityModule.getName()).getCompleteColumnName() + " DESC");
		}
		return false;
	}

}
