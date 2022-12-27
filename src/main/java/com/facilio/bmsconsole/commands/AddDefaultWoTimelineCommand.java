package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.recordcustomization.RecordCustomizationValuesContext;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddDefaultWoTimelineCommand extends FacilioCommand {

	private static org.apache.log4j.Logger log = LogManager.getLogger(AddDefaultWoTimelineCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long orgId = (long)context.get("orgId");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioModule module = modBean.getModule(ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldNameVsField = fields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));

		ApplicationContext app = ApplicationApi.getApplicationForLinkName(SignupUtil.getSignupApplicationLinkName());
		ViewGroups viewGroup = new ViewGroups();
		viewGroup.setModuleName(ContextNames.WORK_ORDER);
		viewGroup.setName("systemtimelineviews");
		viewGroup.setDisplayName("System Views");
		viewGroup.setAppId(app.getId());
		viewGroup.setGroupType(ViewGroups.ViewGroupType.TIMELINEVIEW_GROUP.getIntVal());
		long groupId = ViewAPI.addViewGroup(viewGroup, orgId, ContextNames.WORK_ORDER);

		TimelineViewContext timelineView = new TimelineViewContext();
		timelineView.setName("staffviews");
		timelineView.setDisplayName("Staff View");
		timelineView.setGroupId(groupId);
		timelineView.setAppId(app.getId());
		timelineView.setModuleName(ContextNames.WORK_ORDER);
		timelineView.setModuleId(module.getModuleId());

		timelineView.setGroupByFieldId(fieldNameVsField.get("assignedTo").getFieldId());
		timelineView.setStartDateFieldId(fieldNameVsField.get("scheduledStart").getFieldId());
		timelineView.setEndDateFieldId(fieldNameVsField.get("estimatedEnd").getFieldId());

		timelineView.setAllowRescheduling(true);
		timelineView.setAllowGroupAssignment(true);
		timelineView.setAllowReAssignment(true);
		timelineView.setAllowPastAssignment(true);
		timelineView.setViewType(FacilioView.ViewType.TIMELINE.getIntVal());
		timelineView.setType(FacilioView.ViewType.TIMELINE);
		timelineView.setDefault(true);

		List<RecordCustomizationValuesContext> customizations = new ArrayList<RecordCustomizationValuesContext>();

		FacilioChain statusListChain = FacilioChainFactory.getTicketPriorityListChain();
		statusListChain.execute();
		List<TicketPriorityContext> priorities = (List<TicketPriorityContext>) statusListChain.getContext().get(ContextNames.TICKET_PRIORITY_LIST);
		for(TicketPriorityContext priorityField : priorities) {
			RecordCustomizationValuesContext customizationValue = new RecordCustomizationValuesContext();
			customizationValue.setCustomization("{\"eventColor\":\""+priorityField.getColour()+"\"}");
			customizationValue.setFieldValue(String.valueOf(priorityField.getId()));
			customizations.add(customizationValue);
		}


		RecordCustomizationContext customization = new RecordCustomizationContext();
		customization.setCustomizationType(RecordCustomizationContext.CustomizationType.FIELD.getIntVal());
		customization.setCustomizationFieldId(fieldNameVsField.get("priority").getFieldId());
		customization.setDefaultCustomization("{\"eventColor\":\"#4d95ff\"}");
		customization.setValues(customizations);

		timelineView.setRecordCustomization(customization);
		ViewAPI.addView(timelineView, orgId);

		return false;
	}

}
