package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddCVCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//		String name = (String) context.get(FacilioConstants.ContextNames.VIEW_NAME);
		if(view != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long viewId = view.getId();
			Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
			if (view.getCriteria() != null) {
				Criteria viewCriteria = view.getCriteria();
				boolean isPm = moduleName.equals(ContextNames.PREVENTIVE_MAINTENANCE);
				List<String> templateFields = null;
				if (isPm) {
					templateFields = PreventiveMaintenanceAPI.getTemplateFields();
				}
				for (String key : viewCriteria.getConditions().keySet()) {
					Condition condition = viewCriteria.getConditions().get(key);
					String fieldModuleName = moduleName;
					if(isPm) {
						fieldModuleName = PreventiveMaintenanceAPI.getPmModule(templateFields, condition.getFieldName());
					}
					FacilioField field = modBean.getField(condition.getFieldName(), fieldModuleName);
					condition.setField(field);
				}
				if (criteria != null) {
					criteria.andCriteria(viewCriteria);
				}
				else {
					criteria = viewCriteria;
				}
			}
			
			if(view.getIncludeParentCriteria()) {
				FacilioView parentView = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
				if (criteria == null && parentView != null) {
					criteria = parentView.getCriteria();
				}
				else if (parentView != null && parentView.getCriteria() != null) {
					criteria.andCriteria(parentView.getCriteria());
				}				
			}
			view.setCriteria(criteria);
			if(viewId == -1) {
				if(view.getTypeEnum() == null)
				{
					view.setType(ViewType.TABLE_LIST);
				}
				
				
				FacilioModule moduleObj = modBean.getModule(moduleName);
				String extendedModName = null;
				if (moduleObj != null && moduleObj.getExtendModule() != null) {
					extendedModName = moduleObj.getExtendModule().getName();
				}
				if (extendedModName != null && extendedModName.contains("asset")) {	
					view.setModuleName(moduleName);
				}
				if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
					view.setModuleName(moduleName);
				} 
				view.setModuleId(moduleObj.getModuleId());
				
				EventType activityType =  (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
				if (( activityType == null ) || ( activityType == EventType.CREATE )) {
					if(view.getName() == null)
					{
						view.setName(view.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
					}
					view.setDefault(false);
				}
				
				else {
					view.setDefault(true);          
				}
				
				viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			}
			else {
				long viewId1 = ViewAPI.updateView(viewId,view);	
			}
			view.setId(viewId);
			
			ViewAPI.addViewSharing(view);
			
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			
		}
		return false;
	}

}
