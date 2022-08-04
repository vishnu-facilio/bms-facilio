package com.facilio.bmsconsoleV3.commands.labour;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetDefaultValueForSingleLabourCraftRecord extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception{

		Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
		String moduleName = Constants.getModuleName(context);
		List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

		if(CollectionUtils.isNotEmpty(records)) {
			for(ModuleBaseWithCustomFields record : records){
				List<Map<String,Object>> list = record.getSubForm().get(FacilioConstants.CraftAndSKills.LABOUR_CRAFT);
				if(CollectionUtils.isNotEmpty(list) && list.size() == 1){
					list.get(0).put("isDefault",true);
				}
			}
		}

		return false;
	}
}
