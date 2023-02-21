package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.LookupField;
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
				updateConditionField(modBean, moduleName, viewCriteria);

				if (criteria != null) {
					criteria.andCriteria(viewCriteria);
				}
				else {
					criteria = viewCriteria;
				}
			}

			if(view instanceof TimelineViewContext && ((TimelineViewContext) view).getGroupCriteria() != null) {
				TimelineViewContext timelineView = (TimelineViewContext) view;
				FacilioField field = modBean.getField(timelineView.getGroupByFieldId());
				if(field instanceof LookupField) {
					updateConditionField(modBean, ((LookupField)field).getLookupModule().getName(), timelineView.getGroupCriteria());
				}
				else if(field instanceof EnumField) {
					Criteria groupCriteria = timelineView.getGroupCriteria();
					for (String key : groupCriteria.getConditions().keySet()) {
						Condition condition = groupCriteria.getConditions().get(key);
						condition.setField(FieldFactory.getField("index", "IDX", ModuleFactory.getEnumFieldValuesModule(), FieldType.NUMBER));
					}
				}
			}

			if(view.getIncludeParentCriteria()) {
				FacilioView parentView = (FacilioView) context.get(FacilioConstants.ViewConstants.PARENT_VIEW_OBJECT);
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
				view.setDefault((activityType != null) && (activityType != EventType.CREATE));
				
				viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			}
			else {
				long viewId1 = ViewAPI.updateView(viewId,view);	
			}
			view.setId(viewId);

			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			
		}
		return false;
	}

	private static void updateConditionField(ModuleBean modBean, String moduleName, Criteria criteria) throws Exception{
		boolean isPm = moduleName.equals(ContextNames.PREVENTIVE_MAINTENANCE);
		List<String> templateFields = null;
		if (isPm) {
			templateFields = PreventiveMaintenanceAPI.getTemplateFields();
		}
		for (String key : criteria.getConditions().keySet()) {
			Condition condition = criteria.getConditions().get(key);
			String fieldModuleName = moduleName;
			if(isPm) {
				fieldModuleName = PreventiveMaintenanceAPI.getPmModule(templateFields, condition.getFieldName());
			}
			FacilioField field = modBean.getField(condition.getFieldName(), fieldModuleName);
			condition.setField(field);
		}
	}
}
