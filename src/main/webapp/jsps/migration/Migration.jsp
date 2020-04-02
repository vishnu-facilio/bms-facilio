<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>

<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.BooleanField" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>




<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


            FacilioModule alarmOccurrenModule =  modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
            FacilioModule operationalarmoccurrence = new FacilioModule();
            operationalarmoccurrence.setName("operationalarmoccurrence");
            operationalarmoccurrence.setDisplayName("Operation Alarmoccurrence");
            operationalarmoccurrence.setTableName("Operation_Alarm_Occurrence");
            operationalarmoccurrence.setType(FacilioModule.ModuleType.BASE_ENTITY);
            operationalarmoccurrence.setExtendModule(alarmOccurrenModule);
            long operationAlarmOccurrenceId = modBean.addModule(operationalarmoccurrence);
            operationalarmoccurrence.setModuleId(operationAlarmOccurrenceId);


            List<FacilioField> operationAlarmOccurrencefields = new ArrayList<>();
            NumberField coveragetype_occurrence = new NumberField(operationalarmoccurrence,  "coverageType", "Coverage Type",  FacilioField.FieldDisplayType.NUMBER, "COVERAGE_TYPE",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(coveragetype_occurrence);
            operationAlarmOccurrencefields.add(coveragetype_occurrence);


            FacilioModule baseModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
            FacilioModule operationalarm = new FacilioModule();
            operationalarm.setName("operationalarm");
            operationalarm.setDisplayName("Operation Alarm");
            operationalarm.setTableName("Operation_Alarm");
            operationalarm.setType(FacilioModule.ModuleType.BASE_ENTITY);
            operationalarm.setExtendModule(baseModule);
            long operationAlarmId = modBean.addModule(operationalarm);
            operationalarm.setModuleId(operationAlarmId);

            List<FacilioField> operationAlarmfields = new ArrayList<>();
            NumberField coveragetype_Alarm = new NumberField(operationalarm,  "coverageType", "Coverage Type",  FacilioField.FieldDisplayType.NUMBER, "COVERAGE_TYPE",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(coveragetype_Alarm);
            operationAlarmfields.add(coveragetype_Alarm);

            FacilioModule baseeventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
            FacilioModule operationAlarmEvent = new FacilioModule();
            operationAlarmEvent.setName("operationevent");
            operationAlarmEvent.setDisplayName("Operation Alarm Event");
            operationAlarmEvent.setTableName("Operation_Event");
            operationAlarmEvent.setType(FacilioModule.ModuleType.BASE_ENTITY);
            operationAlarmEvent.setExtendModule(baseeventModule);
            long operationAlarmEventId = modBean.addModule(operationAlarmEvent);
            operationAlarmEvent.setModuleId(operationAlarmEventId);

            List<FacilioField> operationAlarmEventfields = new ArrayList<>();
            NumberField coveragetype_Event = new NumberField(operationAlarmEvent,  "coverageType", "Coverage Type",  FacilioField.FieldDisplayType.NUMBER, "COVERAGE_TYPE",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(coveragetype_Event);
            operationAlarmEventfields.add(coveragetype_Event);

            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>