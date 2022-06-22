package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.rules.Constants.FieldFactory;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class FetchTemplateFromResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	/**
		 * this is to command fetch the response's template.
		 * there are times when the parent template's scope got changed. but the response's scope will remain old. (eg. changing removing site in template but already created responses for that site should work)
		 * in such cases template will not get filled automatically, so fetching here with skip scoping. 
		 */
    	
    	List<ResponseContext> responses = Constants.getRecordList((FacilioContext) context);
    	
    	try {
    		if (CollectionUtils.isNotEmpty(responses)) {
        		Map<Long, ResponseContext> parentMissingResponseMap = responses.stream().filter(response -> response.getParent() == null).collect(Collectors.toMap(ResponseContext::getId, Function.identity()));
        		
        		if(MapUtils.isNotEmpty(parentMissingResponseMap)) {
        			
        			List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.QAndA.RESPONSE);
        			
        			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        			
        			SelectRecordsBuilder<ResponseContext> select = new SelectRecordsBuilder<ResponseContext>()
            				.moduleName(FacilioConstants.QAndA.RESPONSE)
            				.select(fields)
            				.andCondition(CriteriaAPI.getIdCondition(parentMissingResponseMap.keySet(), Constants.getModBean().getModule(FacilioConstants.QAndA.RESPONSE)))
            				.skipScopeCriteria()
            				.beanClass(ResponseContext.class)
            				.fetchSupplement((SupplementRecord) fieldMap.get("template"))
            				;
        			
        			List<ResponseContext> unScopedResponses = select.get();
        			
        			if(CollectionUtils.isNotEmpty(unScopedResponses)) {
        				
        				for(ResponseContext unScopedResponse : unScopedResponses) {
        					
        					if(unScopedResponse.getTemplate() != null) {
        						parentMissingResponseMap.get(unScopedResponse.getId()).setParent(unScopedResponse.getTemplate());
        					}
        				}
        			}
        		}
        	}
    	}
    	catch(Exception e) {
    		LOGGER.error(e.getMessage(),e);
    	}
		return false;
	}

}
