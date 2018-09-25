package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetResourcesLatestReadingValuesCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> resourcesId = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(fieldName, moduleName);
		for(Long id : resourcesId) {
			rdmPairs.add(Pair.of(id, field));
		}
		Map<String, ReadingDataMeta> rdms = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
		context.put(FacilioConstants.ContextNames.READINGS, rdms);
		return false;
	}

}
