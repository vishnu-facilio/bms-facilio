<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioChainFactory"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantSpaceContext"%>
<%@page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.db.criteria.operators.BooleanOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.db.criteria.operators.CommonOperators"%>
<%@page import="com.facilio.db.criteria.operators.StringOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="com.facilio.db.util.DBConf"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%
	final class MigrationCommand extends FacilioCommand {
		@Override
		public boolean executeCommand(Context context) throws Exception {
			final Logger LOGGER = LogManager.getLogger(MigrationCommand.class.getName());
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
				qeueuFields.add(FieldFactory.getField("fileId", "FILE_ID", FieldType.NUMBER));

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
				LOGGER.info("@@@@@instnt id value is  "+idVal);
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("FacilioInstantJobQueue_Data")
						.andCondition(CriteriaAPI.getCondition("QUEUE_ID","queueId", StringUtils.join(idVal, ","), StringOperators.IS))
						.orderBy("QUEUE_ID");
				
				List<Map<String,Object>> queueMessages = selectBuilder.get();
				
				LOGGER.info("@@@@@instnt queue "+props);
				LOGGER.info("@@@@@instnt queue_Data "+queueMessages);
				List<Map<String,Object>> insertVal = new ArrayList<>();
				for(Map<String,Object> itrVal:queueMessages){
					long queueId = (long)itrVal.get("queueId");
					String msg = (String) itrVal.get("data");
					long fileId = DBConf.getInstance().addFile(msg);
					LOGGER.info("@@@@@fileId is  "+fileId);
					finalMap.get(queueId).put("fileId", fileId);
					insertVal.add(finalMap.get(queueId));
				}
				
				
				LOGGER.info(" inset value is ::  "+insertVal);
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().fields(qeueuFields).table("InstantJobQueue")
						.addRecords(insertVal);
				insertBuilder.save();
				
			}
			catch(Exception e) {
				LOGGER.info(" Exception occurred in instnt queue "+e);
				throw e;
			}
			return false;	
		}
	}
%>

<%
	FacilioChain c = FacilioChain.getTransactionChain();
	c.addCommand(new MigrationCommand());
	c.execute();
%>

