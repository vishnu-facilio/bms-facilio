package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PMTriggerV2.PMTriggerFrequency;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class GetAvailableTriggerFrequencyForPM extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	Long pmId = (Long) context.get("pmId");
    	
    	ModuleBean modbean = Constants.getModBean();
    	
    	List<FacilioField> fields = modbean.getAllFields(FacilioConstants.PM_V2.PM_V2_TRIGGER);
    	
    	Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    	
    	SelectRecordsBuilder<PMTriggerV2> select = new SelectRecordsBuilder<PMTriggerV2>()
    			.module(modbean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER))
    			.select(fields)
    			.beanClass(PMTriggerV2.class)
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmId+"", NumberOperators.EQUALS))
    			;
    			
    	List<PMTriggerV2> triggers = select.get();
    	
    	List<Integer> availableList = new ArrayList<>();
    	availableList.add(PMTriggerFrequency.DO_NOT_REPEAT.getIndex()-1);
    	availableList.add(PMTriggerFrequency.DAILY.getIndex()-1);
    	availableList.add(PMTriggerFrequency.WEEKLY.getIndex()-1);
    	availableList.add(PMTriggerFrequency.MONTHLY.getIndex()-1);
    	availableList.add(PMTriggerFrequency.QUARTERLY.getIndex()-1);
    	availableList.add(PMTriggerFrequency.HALF_YEARLY.getIndex()-1);
    	availableList.add(PMTriggerFrequency.ANNUALLY.getIndex()-1);
    			
    	for(PMTriggerV2 trigger : triggers) {
    		availableList.remove(trigger.getFrequency());
    	}
    	
    	context.put(FacilioConstants.PM_V2.AVAILABLE_TRIGGER_FREQUENCIES, availableList);
    	
		return false;
	}

}
