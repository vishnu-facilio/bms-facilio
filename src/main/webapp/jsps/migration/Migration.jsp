<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.bmsconsole.commands.TransactionChainFactory" %>
<%@ page import="com.facilio.bmsconsole.context.AlarmContext" %>
<%@ page import="com.facilio.bmsconsole.context.BMSEventContext" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.chain.FacilioContext" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@ page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@ page import="com.facilio.events.constants.EventConstants" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.SelectRecordsBuilder" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.commons.collections4.CollectionUtils" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>


<%
	AccountUtil.setCurrentAccount(78);
	FacilioChain chain = FacilioChain.getTransactionChain();
	chain.addCommand(new FacilioCommand() {
		@Override
		public boolean executeCommand(Context context) throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
					.module(modBean.getModule(FacilioConstants.ContextNames.ALARM))
					.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM))
					.beanClass(AlarmContext.class)
					.andCondition(CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "4", NumberOperators.EQUALS));
			List<AlarmContext> alarmContexts = builder.get();
			if (CollectionUtils.isNotEmpty(alarmContexts)) {
				List<BMSEventContext> events = new ArrayList<>();
				for (AlarmContext alarmContext : alarmContexts) {
					BMSEventContext bmsEventContext = new BMSEventContext();
					bmsEventContext.setEventMessage(alarmContext.getSubject());
					bmsEventContext.setResource(alarmContext.getResource());
					bmsEventContext.setCondition(alarmContext.getCondition());
					bmsEventContext.setControllerId(alarmContext.getControllerId());
					bmsEventContext.setSource(alarmContext.getSource());
					bmsEventContext.setCreatedTime(alarmContext.getCreatedTime());
					bmsEventContext.setSeverityString("Major");
					bmsEventContext.setDescription(alarmContext.getDescription());
					bmsEventContext.setMessage(alarmContext.getSubject());
					bmsEventContext.setDescription(alarmContext.getDescription());
					bmsEventContext.setPossibleCause(alarmContext.getPossibleCauses());
					bmsEventContext.setRecommendation(alarmContext.getRecommendation());
					events.add(bmsEventContext);

					if (alarmContext.getCreatedTime() != -1) {
						bmsEventContext = new BMSEventContext();
						bmsEventContext.setEventMessage(alarmContext.getSubject());
						bmsEventContext.setResource(alarmContext.getResource());
						bmsEventContext.setCondition(alarmContext.getCondition());
						bmsEventContext.setControllerId(alarmContext.getControllerId());
						bmsEventContext.setSource(alarmContext.getSource());
						bmsEventContext.setCreatedTime(alarmContext.getClearedTime());
						bmsEventContext.setSeverityString("Clear");
						bmsEventContext.setDescription(alarmContext.getDescription());
						bmsEventContext.setMessage(alarmContext.getSubject());
						bmsEventContext.setDescription(alarmContext.getDescription());
						bmsEventContext.setPossibleCause(alarmContext.getPossibleCauses());
						bmsEventContext.setRecommendation(alarmContext.getRecommendation());
						if (StringUtils.isNotEmpty(alarmContext.getAdditionalInfoJsonStr())) {
							bmsEventContext.setAdditionalInfoJsonStr(alarmContext.getAdditionalInfoJsonStr());
						}
						events.add(bmsEventContext);
					}
				}

				FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain();
				FacilioContext eventContext = addEventChain.getContext();
				eventContext.put(EventConstants.EventContextNames.EVENT_LIST, events);
				addEventChain.execute();
			}
			return false;
		}
	});
	chain.execute();
%>
