package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetPmPlannerTriggerWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, getPMPlannerTriggerDetailsWidget());
        return false;
    }

    private SummaryWidget getPMPlannerTriggerDetailsWidget() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmv2Module = moduleBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        FacilioModule pmV2triggerModule = moduleBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);
        FacilioModule pmV2PlannerModule = moduleBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER);
        List<FacilioField> pmV2triggerFieldsList = moduleBean.getAllFields(FacilioConstants.PM_V2.PM_V2_TRIGGER);
        Map<String, FacilioField> pmV2triggerFieldsMap = FieldFactory.getAsMap(pmV2triggerFieldsList);

        List<FacilioField> pmV2PlannerFieldsList = moduleBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER);
        Map<String, FacilioField> pmV2PlannerFieldsMap = FieldFactory.getAsMap(pmV2PlannerFieldsList);

        FacilioField frequencyField = pmV2triggerFieldsMap.get("frequency");
        FacilioField startTimeField = pmV2triggerFieldsMap.get("startTime");
        FacilioField endTimeField = pmV2triggerFieldsMap.get("endTime");

        // Scheduler Objects are mocked as fields
        FacilioField mockFrequencyField = SignupUtil.getStringField(null, "frequency", frequencyField.getDisplayName(),
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());
        FacilioField mockSkipScheduleInfoObjectAsField = SignupUtil.getStringField(null, "skipEvery", "Skip",
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());
        FacilioField mockRunEveryScheduleInfoObjectAsField = SignupUtil.getStringField(null, "runEvery", "Run Every",
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());

        // Scheduler Objects are mocked as fields
        FacilioField mockSeasonNameField = SignupUtil.getStringField(null, "seasonName", "Season Name",
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());
        FacilioField mockSeasonStartField = SignupUtil.getStringField(null, "seasonStart", "Season Start",
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());
        FacilioField mockSeasonEndField = SignupUtil.getStringField(null, "seasonEnd", "Season End",
                null, FacilioField.FieldDisplayType.TEXTBOX, false, false, false,
                false, pmv2Module.getOrgId());

        FacilioField resourceCountField = pmV2PlannerFieldsMap.get("resourceCount");

        SummaryWidget pageWidget = new SummaryWidget();

        // Trigger Summary Group
        SummaryWidgetGroup triggerSummaryWidgetGroup = new SummaryWidgetGroup();
        triggerSummaryWidgetGroup.setDisplayName("Trigger Summary");
        triggerSummaryWidgetGroup.setColumns(4);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(triggerSummaryWidgetGroup, mockFrequencyField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(triggerSummaryWidgetGroup, startTimeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(triggerSummaryWidgetGroup, endTimeField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(triggerSummaryWidgetGroup, mockSkipScheduleInfoObjectAsField, 1, 4, 1);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(triggerSummaryWidgetGroup, mockRunEveryScheduleInfoObjectAsField, 2, 1, 1);


        // Seasonal Trigger Summary Group
        SummaryWidgetGroup seasonalTriggerSummaryWidgetGroup = new SummaryWidgetGroup();
        seasonalTriggerSummaryWidgetGroup.setDisplayName("Season Details");
        seasonalTriggerSummaryWidgetGroup.setName("seasonDetailWidgetGroup");
        seasonalTriggerSummaryWidgetGroup.setColumns(3);

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(seasonalTriggerSummaryWidgetGroup, mockSeasonNameField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(seasonalTriggerSummaryWidgetGroup, mockSeasonStartField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(seasonalTriggerSummaryWidgetGroup, mockSeasonEndField, 1, 3, 1);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(triggerSummaryWidgetGroup);
        widgetGroupList.add(seasonalTriggerSummaryWidgetGroup);

        pageWidget.setDisplayName("Trigger Summary");
        pageWidget.setModuleId(pmv2Module.getModuleId());
        pageWidget.setGroups(widgetGroupList);

        return pageWidget;
    }
}
