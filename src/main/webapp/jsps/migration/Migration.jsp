
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@page import="com.facilio.bmsconsole.util.TenantsAPI"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@ page import="com.facilio.modules.fields.EnumField" %>
<%@page import="com.facilio.modules.fields.EnumFieldValue"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@page import="java.util.ArrayList"%>
<%@ page import="java.util.List" %>

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

            final Logger LOGGER = LogManager.getLogger(TenantsAPI.class.getName());
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule metricsModule = modBean.getModule("agentMetrics");
                if(metricsModule == null){
                    metricsModule = new FacilioModule();
                    metricsModule.setName("agentMetrics");
                    metricsModule.setDisplayName("Agent Metrics");
                    metricsModule.setTableName("Agent_V2_Metrics");
                    metricsModule.setType(3);
                    long moduleId = modBean.addModule(metricsModule);
                    if(moduleId > 0){
                        metricsModule.setModuleId(moduleId);

                        NumberField agentIdField = new NumberField(metricsModule,"agentId","Agent Id", FacilioField.FieldDisplayType.NUMBER,"AGENT_ID", FieldType.NUMBER,true,false,true,false);
                        modBean.addField(agentIdField);

                        List<EnumFieldValue> publishTypes = new ArrayList<>();
                        publishTypes.add(new EnumFieldValue(0+1,"CUSTOM",1,true));
                        publishTypes.add(new EnumFieldValue(1+1,"ACK",2,true));
                        publishTypes.add(new EnumFieldValue(2+1,"AGENT",3,true));
                        publishTypes.add(new EnumFieldValue(3+1,"COV",4,true));
                        publishTypes.add(new EnumFieldValue(4+1,"DEVICE_POINTS",5,true));
                        publishTypes.add(new EnumFieldValue(5+1,"EVENTS",6,true));
                        publishTypes.add(new EnumFieldValue(6+1,"TIMESERIES",7,true));
                        publishTypes.add(new EnumFieldValue(7+1,"CONTROLLERS",8,true));

                        com.facilio.modules.fields.EnumField publishTypeField = new EnumField(metricsModule,"publishType","Publish Type", FacilioField.FieldDisplayType.SELECTBOX,"PUBLISH_TYPE",FieldType.ENUM,true,false,true,false,publishTypes);
                        modBean.addField(publishTypeField);

                        NumberField numOfMsgField = new NumberField(metricsModule, "numberOfMessages", "Number of Messages", FacilioField.FieldDisplayType.NUMBER, "NO_OF_MSGS", FieldType.NUMBER, true, false, true,false);
                        modBean.addField(numOfMsgField);

                        NumberField sizeField = new NumberField( metricsModule, "size", "Size", FacilioField.FieldDisplayType.NUMBER, "DATA_SIZE", FieldType.NUMBER, true, false, true,false);
                        modBean.addField(sizeField);

                        FacilioField lastModTimeField = new FacilioField(metricsModule, "lastUpdatedTime", "Last Updated Time", FacilioField.FieldDisplayType.NUMBER, "LAST_UPDATED_TIME", FieldType.DATE_TIME, true, false, true,false);
                        modBean.addField(lastModTimeField);

                        FacilioField createdTimeField = new FacilioField(metricsModule, "createdTime", "Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true,false);
                        modBean.addField(createdTimeField);

                    }else {
                        LOGGER.info(" module id can't be less than 1");
                    }
                }
            }catch (Exception e){
                LOGGER.info("Exception while migrating AgentMetrics fields ");
            }

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first


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
