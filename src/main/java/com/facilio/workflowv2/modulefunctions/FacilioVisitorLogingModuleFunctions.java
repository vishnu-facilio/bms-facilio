package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.scriptengine.context.ScriptContext;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptModule;

@ScriptModule(moduleName = FacilioConstants.ContextNames.VISITOR_LOGGING)
public class FacilioVisitorLogingModuleFunctions extends FacilioModuleFunctionImpl {

	@Override
	public void add(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		Object insertObject = objects.get(1);
		
		List<VisitorLoggingContext> visitorLoggingContexts = new ArrayList<VisitorLoggingContext>();
		if(insertObject instanceof Map) {

			VisitorLoggingContext visitorLoggingContext = FieldUtil.getAsBeanFromMap((Map<String, Object>) insertObject, VisitorLoggingContext.class);
			
			visitorLoggingContexts.add(visitorLoggingContext);
		}
		else if (insertObject instanceof Collection) {
			
			List<Object> insertList = (List<Object>)insertObject;
			
			for(Object insert :insertList) {
				VisitorLoggingContext visitorLoggingContext = FieldUtil.getAsBeanFromMap((Map<String, Object>) insert, VisitorLoggingContext.class);
				visitorLoggingContexts.add(visitorLoggingContext);
			}
		}
		
		if(!CollectionUtils.isEmpty(visitorLoggingContexts)) {
			
			FacilioChain c = TransactionChainFactory.addVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			
			for(VisitorLoggingContext visitorLoggingContext : visitorLoggingContexts) {
				visitorLoggingContext.parseFormData();
			}
			
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingContexts);
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingContexts);
				
			c.execute();
			if(CollectionUtils.isNotEmpty(visitorLoggingContexts)) {
				scriptContext.incrementTotalInsertCount(visitorLoggingContexts.size());
			}
		}
	}
}
