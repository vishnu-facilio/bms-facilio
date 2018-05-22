package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingDataMetaCommand implements Command {

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
			Map<String, ReadingDataMeta> readingDataMeta = new HashMap<> ();
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				List<Long> resourceList=new ArrayList<Long>();
				for(ReadingContext reading : readings) {
					resourceList.add(reading.getParentId());
				}
				List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList( resourceList, allFields) ;
				for(ReadingDataMeta meta : metaList) {
					long resourceId = meta.getResourceId();
					long fieldId = meta.getField().getFieldId();
					readingDataMeta.put(resourceId+"_"+fieldId, meta);
				}
			}
			context.put(FacilioConstants.ContextNames.READING_DATA_META, readingDataMeta);
		}
		return false;
	}
	

}
