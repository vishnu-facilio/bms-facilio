<%@page import="com.facilio.constants.FacilioConstants.TicketStatus" %>
<%@page import="com.facilio.chain.FacilioContext" %>
<%@page import="com.facilio.modules.FieldFactory" %>
<%@page import="java.util.Collections" %>
<%@page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@page import="com.facilio.modules.FieldUtil" %>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.EventType" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.modules.FacilioStatus" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext" %>
<%@page import="org.apache.commons.collections4.CollectionUtils" %>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext" %>
<%@page import="com.facilio.modules.SelectRecordsBuilder" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
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
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.SelectRecordsBuilder" %>
<%@ page import="com.facilio.bmsconsole.actions.RollUpRecommendedRuleAction" %>
<%@ page import="com.facilio.bmsconsole.commands.TransactionChainFactory" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabGroupContext" %>
<%@ page import="com.facilio.bmsconsole.context.ApplicationContext" %>
<%@ page import="com.facilio.bmsconsole.util.*" %>
<%@ page import="com.facilio.bmsconsole.context.WebTabContext" %>
<%@ page import="com.facilio.accounts.dto.NewPermission" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="com.facilio.fs.FileInfo" %>
<%@ page import="com.facilio.services.factory.FacilioFactory" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="com.facilio.services.filestore.FileStore" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@ page import="com.facilio.db.criteria.operators.CommonOperators"%>
<%@ page import="com.facilio.db.criteria.operators.StringOperators"%>
<%@ page import="com.facilio.db.util.DBConf"%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
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
        	try{

				List<FacilioField> qeueuFields = new ArrayList<>();
				qeueuFields.add(FieldFactory.getField("id", "ID", FieldType.ID));
				qeueuFields.add(FieldFactory.getField("orgId", "ORGID",FieldType.NUMBER));
				qeueuFields.add(FieldFactory.getField("addedTime", "ADDED_TIME", FieldType.DATE_TIME));
				qeueuFields.add(FieldFactory.getField("visibilityTimeout", "VISIBILITY_TIMEOUT", FieldType.NUMBER));
				qeueuFields.add(FieldFactory.getField("lastClientReceivedTime", "LAST_CLIENT_RECEIVED_TIME", FieldType.DATE_TIME));
				qeueuFields.add(FieldFactory.getField("maxClientReceiptCount", "MAX_CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
				qeueuFields.add(FieldFactory.getField("clientReceiptCount", "CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
				qeueuFields.add(FieldFactory.getField("deletedTime", "DELETED_TIME", FieldType.DATE_TIME));

				List<FacilioField> qeueuNewFields = new ArrayList<>();
				qeueuNewFields.add(FieldFactory.getField("id", "ID", FieldType.ID));
				qeueuNewFields.add(FieldFactory.getField("orgId", "ORGID",FieldType.NUMBER));
				qeueuNewFields.add(FieldFactory.getField("addedTime", "ADDED_TIME", FieldType.DATE_TIME));
				qeueuNewFields.add(FieldFactory.getField("visibilityTimeout", "VISIBILITY_TIMEOUT", FieldType.NUMBER));
				qeueuNewFields.add(FieldFactory.getField("lastClientReceivedTime", "LAST_CLIENT_RECEIVED_TIME", FieldType.DATE_TIME));
				qeueuNewFields.add(FieldFactory.getField("maxClientReceiptCount", "MAX_CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
				qeueuNewFields.add(FieldFactory.getField("clientReceiptCount", "CLIENT_RECEIPT_COUNT", FieldType.NUMBER));
				qeueuNewFields.add(FieldFactory.getField("deletedTime", "DELETED_TIME", FieldType.DATE_TIME));
				qeueuNewFields.add(FieldFactory.getField("fileId", "FILE_ID", FieldType.NUMBER));

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("queueId", "QUEUE_ID", FieldType.NUMBER));
				fields.add(FieldFactory.getField("data", "DATA", FieldType.STRING));


				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(qeueuFields).table("FacilioInstantJobQueue")
						.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", null,CommonOperators.IS_EMPTY))
						.orderBy("ID");
				List<Map<String,Object>> props = builder.get();
				Map<Long,Map<String,Object>> finalMap = new HashMap<>();
				List<Long> idVal = new ArrayList<>();	
				for(Map<String,Object> itr:props){
					long id = (long)itr.get("id");
					finalMap.put(id,itr);
					idVal.add(id);	
				}
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("FacilioInstantJobQueue_Data")
						.andCondition(CriteriaAPI.getCondition("QUEUE_ID","queueId", StringUtils.join(idVal, ","), StringOperators.IS))
						.orderBy("QUEUE_ID");
				
				List<Map<String,Object>> queueMessages = selectBuilder.get();
				
				List<Map<String,Object>> insertVal = new ArrayList<>();
				for(Map<String,Object> itrVal:queueMessages){
					long queueId = (long)itrVal.get("queueId");
					String msg = (String) itrVal.get("data");
					long fileId = DBConf.getInstance().addFile(msg);
					finalMap.get(queueId).put("fileId", fileId);
					insertVal.add(finalMap.get(queueId));
				}
				
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().fields(qeueuNewFields).table("InstantJobQueue")
						.addRecords(insertVal);
				insertBuilder.save();

            }
            catch(Exception e) {
                LOGGER.info(e.getMessage());
            }
            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
            return false;
        }
    }
%>

<%
   // List<Organization> orgs = null;
    try {
       // orgs = AccountUtil.getOrgBean().getOrgs();
        //for (Organization org : orgs) {
         //   System.out.println("org: " + org.getOrgId());
       //     AccountUtil.setCurrentAccount(org.getOrgId());
            FacilioChain c = FacilioChain.getTransactionChain();
            c.addCommand(new OrgLevelMigrationCommand());
            c.execute();

       //     AccountUtil.cleanCurrentAccount();
       // }
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("Done");
%>
