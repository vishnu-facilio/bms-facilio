<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.SystemEnumField" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

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
            try{
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<FacilioModule> modulesList =  modBean.getModuleList(FacilioModule.ModuleType.READING,false);
                modulesList.addAll(modBean.getModuleList(FacilioModule.ModuleType.SCHEDULED_FORMULA,false));
                modulesList.addAll(modBean.getModuleList(FacilioModule.ModuleType.LIVE_FORMULA,false));
                modulesList.addAll(modBean.getModuleList(FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA,false));
                for (FacilioModule module: modulesList) {
                    List<FacilioField> fields = modBean.getAllFields(module.getName());
                    Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                    LOGGER.info(fieldMap.containsKey("sourceType") + "  " + fieldMap.containsKey("sourceId"));
                    if (fieldMap.containsKey("sourceType") && fieldMap.containsKey("sourceId")) {
                        LOGGER.info("######Check 1 Module Name "+module.getName() + " - ModuleId " + module.getModuleId() + " - Org id - " + module.getOrgId() + " -- contains source Type and Source Id");
                    } else if (fieldMap.containsKey("sourceType") && !fieldMap.containsKey("sourceId")){
                        LOGGER.info("######Check 2 Module Name "+module.getName() + " - ModuleId " + module.getModuleId() + " - Org id - " + module.getOrgId() + " -- contains source Type");
                        LOGGER.info("Adding Source Id");
                        NumberField sourceIdField = new NumberField(module, "sourceId", "Source Id", FacilioField.FieldDisplayType.NUMBER, "SOURCE_ID", FieldType.NUMBER, false, false, true, false);
                        modBean.addField(sourceIdField);
                    } else if (!fieldMap.containsKey("sourceType") && fieldMap.containsKey("sourceId")) {
                        LOGGER.info("######Check 3 Module Name "+module.getName() + " - ModuleId " + module.getModuleId() + " - Org id - " + module.getOrgId() + " -- contains source Id");
                        LOGGER.info("Adding Source Type");
                        SystemEnumField sourceTypeField = (SystemEnumField) FieldFactory.getField("sourceType","Source Type", "SOURCE_TYPE", module,FieldType.SYSTEM_ENUM);
                        sourceTypeField.setEnumName("SourceType");
                        sourceTypeField.setDefault(true);;
                        modBean.addField(sourceTypeField);
                    } else {
                        LOGGER.info("## Adding Source Type and Source Id ##");
                        NumberField sourceIdField = new NumberField(module, "sourceId", "Source Id", FacilioField.FieldDisplayType.NUMBER, "SOURCE_ID", FieldType.NUMBER, false, false, true, false);
                        modBean.addField(sourceIdField);
                        SystemEnumField sourceTypeField = (SystemEnumField) FieldFactory.getField("sourceType","Source Type", "SOURCE_TYPE", module,FieldType.SYSTEM_ENUM);
                        sourceTypeField.setEnumName("SourceType");
                        sourceTypeField.setDefault(true);;
                        modBean.addField(sourceTypeField);
                    }
                }

            }
            catch(Exception e) {
                LOGGER.info(e.getMessage());
            }



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