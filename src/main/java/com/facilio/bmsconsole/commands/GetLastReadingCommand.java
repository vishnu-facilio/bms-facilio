package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetLastReadingCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
		if(readings == null) {
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			if(reading != null) {
				readings = Collections.singletonList(reading);
			}
		}
		if(readings == null || readings.isEmpty()) {
			return false;
		}
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields= bean.getAllFields(moduleName);
		
		Map<Long,String> fieldVsName =new HashMap<Long,String>();
		
		for(FacilioField field:allFields) {
			fieldVsName.put(field.getFieldId(), field.getName());
		}
		
		Set<Long> fieldList= fieldVsName.keySet();
		List<Long> resourceList=new ArrayList<Long>();
		for(ReadingContext reading : readings) {
			resourceList.add(reading.getParentId());
		}
		
		List<Map<String, Object>> lastReadings= FieldUtil.getLastReading( resourceList, fieldList) ;
        Map<String, Map<String,Object>> lastReadingMap= new HashMap<String,Map<String,Object>> ();
		
		for(Map<String,Object> stats:lastReadings) {
			
			Long resourceId=(long)stats.remove("resourceId");
			Long fieldId=(long)stats.remove("fieldId");
			String fieldName=fieldVsName.get(fieldId);
			lastReadingMap.put(resourceId+"_"+fieldName, stats);
		}
		context.put(FacilioConstants.ContextNames.LAST_READINGS, lastReadingMap);
		return false;
	}
	

}
