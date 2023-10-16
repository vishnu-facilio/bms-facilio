package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;

public class AddWoViewScheduleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long viewId;
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		FacilioView view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId(), -1);
		if (view == null) {
			view = ViewFactory.getView(module, viewName, modBean);
		}
		if ((view != null) && (view.getId() == -1)) {
			viewId = ViewAPI.checkAndAddView(view.getName(), moduleName, null, null, -1);
			view.setId(viewId);
		}
		else {
			viewId = view.getId();
		}
		if (viewId > 0) {
			int fileFormat = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
			EMailTemplate emailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
			
			long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
			ScheduleInfo scheduleInfo = (ScheduleInfo) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
			long endTime = -1;
			if ( context.containsKey(FacilioConstants.ContextNames.END_TIME)) {
				endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
			}
			int maxCount = -1;
			if ( context.containsKey(FacilioConstants.ContextNames.MAX_COUNT)) {
				maxCount = (int) context.get(FacilioConstants.ContextNames.MAX_COUNT);
			}
			
			Map<String, Object> props=new HashMap<String,Object>();
			props.put("viewId", viewId);
			props.put("fileFormat", fileFormat);
			props.put("templateId", emailTemplate.getId());
			props.put("moduleID", moduleId);
			props.put("createdBy", AccountUtil.getCurrentUser().getPeopleId());
			
			FacilioModule viewInfoModule = ModuleFactory.getViewScheduleInfoModule();
			List<FacilioField> fields = FieldFactory.getViewScheduleInfoFields();
			
			EventType type = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			
			long jobId;
			if (type != EventType.EDIT) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(viewInfoModule.getTableName())
						.fields(fields);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				jobId = (long) props.get("id");
			}
			else {
				Long recordIds = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				jobId = recordIds;
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(viewInfoModule.getTableName())
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(jobId, viewInfoModule));

				int count = updateBuilder.update(props);
				
				context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
				Long templateId = (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
				TemplateAPI.deleteTemplate(templateId);
				
			}
			
			if (maxCount != -1) {
				FacilioTimer.scheduleCalendarJob(jobId, "ViewEmailScheduler", startTime, scheduleInfo, "facilio", maxCount);
			}
			else {
				FacilioTimer.scheduleCalendarJob(jobId, "ViewEmailScheduler", startTime, scheduleInfo, "facilio", endTime);
			}
		}
		return false;
	}

}
