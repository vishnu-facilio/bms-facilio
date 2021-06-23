package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BimIntegrationLogsContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateModulesNoInBimIntegrationLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long bimImportId = (Long) context.get(FacilioConstants.ContextNames.BIM_IMPORT_ID);
		Set<String> selectedSheetNames = (Set<String>)context.get(FacilioConstants.ContextNames.SELECTED_SHEET_NAMES);
		
		FacilioModule module = ModuleFactory.getBimIntegrationLogsModule();
		List<FacilioField> fields =  FieldFactory.getBimIntegrationLogsFields();
		
		BimIntegrationLogsContext bimIntegrationLog=new BimIntegrationLogsContext();
		bimIntegrationLog.setId(bimImportId);
		List<String> modules = new ArrayList<>();
		for(String sheetName:selectedSheetNames){
			String moduleNameStr = BimAPI.getModuleNameBySheetName(sheetName);
			String[] modulesArr = moduleNameStr.split("&&");
			modules.addAll(Arrays.asList(modulesArr));
		}
		bimIntegrationLog.setNoOfModules(modules.size());
		
		BimAPI.updateBimIntegrationLog(module, fields, bimIntegrationLog);
		
		return false;
	}

}
