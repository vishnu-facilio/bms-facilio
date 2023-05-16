package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
@Log4j
public class AddControlActionCommandModule extends BaseModuleConfig {

    public AddControlActionCommandModule() {
        setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlActionCommandModule = new ArrayList<FacilioView>();
        controlActionCommandModule.add(getUpcomingControlCommandView().setOrder(order++));
        controlActionCommandModule.add(getHistoryControlCommandView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        groupDetails.put("views", controlActionCommandModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getUpcomingControlCommandView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("upcoming");
        allView.setDisplayName("Upcoming Commands");
        allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        Criteria criteria = new Criteria();

        List<Integer> upcomingStatusInt = new ArrayList<Integer>();

        upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED.getIntVal());
        upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED_WITH_NO_PERMISSION.getIntVal());

        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(upcomingStatusInt, ","), NumberOperators.EQUALS));

        allView.setCriteria(criteria);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
    private static FacilioView getHistoryControlCommandView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("history");
        allView.setDisplayName("Command History");
        allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        allView.setSortFields(sortFields);
        allView.setFields(getAllViewColumns());

        List<Integer> historyStatusInt = new ArrayList<Integer>();

        historyStatusInt.add(ControlActionCommandContext.Status.SUCCESS.getIntVal());
        historyStatusInt.add(ControlActionCommandContext.Status.ERROR.getIntVal());
        historyStatusInt.add(ControlActionCommandContext.Status.SENT.getIntVal());

        Criteria criteria = new Criteria();

        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(historyStatusInt, ","), NumberOperators.EQUALS));

        allView.setCriteria(criteria);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("resource", "Asset"));
        columns.add(new ViewField("command", "Set Value"));
        columns.add(new ViewField("executedTime", "Time"));
        columns.add(new ViewField("status", "Status"));
        columns.add(new ViewField("executedBy", "Executed By"));
        return columns;
    }
    @Override
    public void addData() throws Exception {
        addControlActionCommandModule();
        LOGGER.info("Control Action Command Module added successfully");
    }

    private void addControlActionCommandModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule("controlActionCommand","Control Action Command","Control_Action_Command",FacilioModule.ModuleType.BASE_ENTITY,true);

        FacilioModule controlSchedule = modBean.getModule("controlSchedule");
        FacilioModule controlGroup = modBean.getModule("controlGroupv2");
        FacilioModule controlException = modBean.getModule("controlScheduleException");
        FacilioModule controlRoutine = modBean.getModule("controlGroupRoutine");


        List<FacilioField> fields = new ArrayList<>();

        LookupField executedBy = FieldFactory.getDefaultField("executedBy","Executed By","EXECUTED_BY",module, FieldType.LOOKUP);
        executedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        executedBy.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.USERS));
        executedBy.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(executedBy);

        SystemEnumField executedMode = FieldFactory.getDefaultField("executedMode","Executed Mode","EXECUTED_MODE",FieldType.SYSTEM_ENUM);
        executedMode.setEnumName("ControlActionExecuteMode");
        executedMode.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(executedMode);

        DateField executedTime = FieldFactory.getDefaultField("executedTime","Executed Time","EXECUTED_TIME",FieldType.DATE_TIME);
        executedTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(executedTime);

        SystemEnumField controlActionMode = FieldFactory.getDefaultField("controlActionMode","Control Action Mode","CONTROL_ACTION_MODE",FieldType.SYSTEM_ENUM);
        controlActionMode.setEnumName("ControlActionMode");
        controlActionMode.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(controlActionMode);

        StringField command = FieldFactory.getDefaultField("command","Command","COMMAND",FieldType.STRING);
        command.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(command);

        LookupField resource = FieldFactory.getDefaultField("resource","Asset","RESOURCE_ID",FieldType.LOOKUP);
        resource.setLookupModuleId(modBean.getModule("resource").getModuleId());
        resource.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        resource.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(resource);

        NumberField fieldId = FieldFactory.getDefaultField("fieldId","Field Id","FIELD_ID",FieldType.NUMBER);
        fieldId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(fieldId);

        StringField value = FieldFactory.getDefaultField("value","Value","VALUE",FieldType.STRING);
        value.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(value);

        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
        status.setEnumName("CommandStatus");
        status.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(status);

        NumberField iotMessageId = FieldFactory.getDefaultField("iotMessageId","IOT Message Id","IOT_MESSAGE_ID",FieldType.NUMBER);
        iotMessageId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(iotMessageId);

        LookupField schedule = FieldFactory.getDefaultField("schedule","Control Schedule","SCHEDULE_ID",FieldType.LOOKUP);
        schedule.setLookupModule(controlSchedule);
        schedule.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(schedule);

        LookupField group = FieldFactory.getDefaultField("group","Control Group","GROUP_ID",FieldType.LOOKUP);
        group.setLookupModule(controlGroup);
        group.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(group);

        LookupField exception = FieldFactory.getDefaultField("exception","Control Exception","EXCEPTION_ID",FieldType.LOOKUP);
        exception.setLookupModule(controlException);
        exception.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(exception);

        LookupField routine = FieldFactory.getDefaultField("routine","Control Routine","ROUTINE_ID",FieldType.LOOKUP);
        routine.setLookupModule(controlRoutine);
        routine.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(routine);

        LookupField sysCreatedBy = FieldFactory.getDefaultField("sysCreatedBy","Created By","SYS_CREATED_BY",FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        sysCreatedBy.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.USERS));
        sysCreatedBy.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(sysCreatedBy);

        LookupField sysModifiedBy = FieldFactory.getDefaultField("sysModifiedBy","Modified By","SYS_MODIFIED_BY",FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        sysModifiedBy.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.USERS));
        sysModifiedBy.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(sysModifiedBy);

        DateField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime","Created Time","SYS_CREATED_TIME",FieldType.DATE_TIME);
        sysCreatedTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(sysCreatedTime);

        DateField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime","Modified Time","SYS_MODIFIED_TIME",FieldType.DATE_TIME);
        sysModifiedTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(sysModifiedTime);

        NumberField retriedCount = FieldFactory.getDefaultField("retriedCount","Retried Count","RETRIED_COUNT",FieldType.NUMBER);
        retriedCount.setAccessType(calculateAccessType(FacilioField.AccessType.READ, FacilioField.AccessType.CRITERIA));
        fields.add(retriedCount);

        module.setFields(fields);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();

        modBean.addSubModule(controlSchedule.getModuleId(),module.getModuleId());
        modBean.addSubModule(controlGroup.getModuleId(),module.getModuleId());
        modBean.addSubModule(controlException.getModuleId(),module.getModuleId());
        modBean.addSubModule(controlRoutine.getModuleId(),module.getModuleId());

    }
    private long calculateAccessType(FacilioField.AccessType... accessTypes) {
        long result = 0;
        for (FacilioField.AccessType accessType : accessTypes) {
            result += accessType.getVal();
        }
        return result;
    }
}
