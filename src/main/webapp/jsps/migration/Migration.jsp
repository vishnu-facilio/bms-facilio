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

            List<FacilioModule> modules = Arrays.asList(
                    modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_CATEGORY),
                    modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM),
                    modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT)
            );

            FacilioModule alarmOccurrenModule =  modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
            FacilioModule readingCatogory =  modBean.getModule(FacilioConstants.ContextNames.READING_ALARM_CATEGORY);


            FacilioModule prealarmoccurrence = new FacilioModule();
            prealarmoccurrence.setName("prealarmoccurrence");
            prealarmoccurrence.setDisplayName("Pre Alarmoccurrence");
            prealarmoccurrence.setTableName("PreAlarmOccurrence");
            prealarmoccurrence.setType(FacilioModule.ModuleType.BASE_ENTITY);
            prealarmoccurrence.setExtendModule(alarmOccurrenModule);
            long preAlarmOccurrenceId = modBean.addModule(prealarmoccurrence);
            prealarmoccurrence.setModuleId(preAlarmOccurrenceId);


            List<FacilioField> preAlarmOccurrencefields = new ArrayList<>();
            LookupField ruleField = new LookupField(prealarmoccurrence, "rule", "Rule", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "RULE_ID", FieldType.LOOKUP, true, false, true, false,"readingrule" );
            modBean.addField(ruleField);
            preAlarmOccurrencefields.add(ruleField);
            LookupField subRuleField = new LookupField(prealarmoccurrence, "subRule", "Sub Rule Id", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "SUB_RULE_ID", FieldType.LOOKUP, true, false, true, false, "readingrule");
            modBean.addField(subRuleField);
            preAlarmOccurrencefields.add(subRuleField);
            NumberField readingIdField = new NumberField(prealarmoccurrence,  "readingFieldId", "Reading Field ID",  FacilioField.FieldDisplayType.NUMBER, "READING_FIELD_ID",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(readingIdField);
            LookupField category = new LookupField(prealarmoccurrence, "readingAlarmCategory", "Category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "CATEGORY_ID", FieldType.LOOKUP, true, false, true, false, readingCatogory);
            modBean.addField(category);
            BooleanField isEventCreated = new BooleanField(prealarmoccurrence,"isReadingEventCreated", "Reading Event Created", FacilioField.FieldDisplayType.DECISION_BOX, "READING_EVENT_CREATED", FieldType.BOOLEAN, false, false, true, false);
            modBean.addField(isEventCreated);


            FacilioModule baseModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
            FacilioModule prealarm = new FacilioModule();
            prealarm.setName("prealarm");
            prealarm.setDisplayName("Pre Alarm");
            prealarm.setTableName("PreAlarm");
            prealarm.setType(FacilioModule.ModuleType.BASE_ENTITY);
            prealarm.setExtendModule(baseModule);
            long preAlarmId = modBean.addModule(prealarm);
            prealarm.setModuleId(preAlarmId);

            LookupField alarmruleField = new LookupField(prealarm, "rule", "Rule", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "RULE_ID", FieldType.LOOKUP, true, false, true, false,"readingrule" );
            modBean.addField(alarmruleField);
            LookupField alarmsubRuleField = new LookupField(prealarm, "subRule", "Sub Rule Id", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "SUB_RULE_ID", FieldType.LOOKUP, true, false, true, false, "readingrule");
            modBean.addField(alarmsubRuleField);
            NumberField alarmreadingIdField = new NumberField(prealarm,  "readingFieldId", "Reading Field ID",  FacilioField.FieldDisplayType.NUMBER, "READING_FIELD_ID",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(alarmreadingIdField);
            LookupField alarmcategory = new LookupField(prealarm, "readingAlarmCategory", "Category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "CATEGORY_ID", FieldType.LOOKUP, true, false, true, false, readingCatogory);
            modBean.addField(alarmcategory);


            FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
            FacilioModule preEvent = new FacilioModule();
            preEvent.setName("preevent");
            preEvent.setDisplayName("Pre Event");
            preEvent.setTableName("PreEvent");
            preEvent.setType(FacilioModule.ModuleType.BASE_ENTITY);
            preEvent.setExtendModule(eventModule);
            long preEventId = modBean.addModule(preEvent);
            preEvent.setModuleId(preEventId);

            LookupField eventruleField = new LookupField(preEvent, "rule", "Rule", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "RULE_ID", FieldType.LOOKUP, true, false, true, false,"readingrule" );
            modBean.addField(eventruleField);
            LookupField eventsubRuleField = new LookupField(preEvent, "subRule", "Sub Rule Id", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "SUB_RULE_ID", FieldType.LOOKUP, true, false, true, false, "readingrule");
            modBean.addField(eventsubRuleField);
            NumberField eventreadingIdField = new NumberField(preEvent,  "readingFieldId", "Reading Field ID",  FacilioField.FieldDisplayType.NUMBER, "READING_FIELD_ID",FieldType.NUMBER ,true, false, true, false);
            modBean.addField(eventreadingIdField);


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