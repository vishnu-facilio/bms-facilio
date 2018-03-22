package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetLastReadingCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, Map<String,Object>> lastReadingMap= new HashMap<String,Map<String,Object>> ();
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
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
				for(Map<String,Object> stats:lastReadings) {
					
					Long resourceId=(long)stats.remove("resourceId");
					Long fieldId=(long)stats.remove("fieldId");
					String fieldName=fieldVsName.get(fieldId);
					lastReadingMap.put(resourceId+"_"+fieldName, stats);
				}
			}
			context.put(FacilioConstants.ContextNames.LAST_READINGS, lastReadingMap);
		}
		return false;
	}
	

}
