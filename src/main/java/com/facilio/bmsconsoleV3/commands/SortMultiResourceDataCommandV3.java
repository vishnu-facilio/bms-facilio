package com.facilio.bmsconsoleV3.commands;

import static org.mockito.ArgumentMatchers.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.MultiResourceContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class SortMultiResourceDataCommandV3 extends FacilioCommand{
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<MultiResourceContext> multiResources = Constants.getRecordList((FacilioContext) context);
		
		if(CollectionUtils.isNotEmpty(multiResources)) {
			multiResources = multiResources.stream()
			        .sorted(Comparator.comparing(MultiResourceContext::getSequence,Comparator.nullsLast(Comparator.naturalOrder())))
			        .collect(Collectors.toList());
		}
		
        String moduleName = Constants.getModuleName(context);
		Map<String, List<MultiResourceContext>> recordMap = new HashMap<>();
		recordMap.put(moduleName, multiResources);
		context.put("recordMap", recordMap);

		return false;		
	}	

}
