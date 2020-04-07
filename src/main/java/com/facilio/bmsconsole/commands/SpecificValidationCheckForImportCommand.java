package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class SpecificValidationCheckForImportCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(SpecificValidationCheckForImportCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		HashMap<String, List<ImportRowContext>> groupedContext = 
				(HashMap<String, List<ImportRowContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT);
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		
		String nameQueryString = StringUtils.join(groupedContext.keySet(), ",");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		if(importProcessContext.getModule() == null){
			FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
			List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
			
			BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
			
			boolean isBim = (bimImport!=null);
			if(isBim){
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
				String moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
				if(moduleName.equals("zonespacerel")){
					module = ModuleFactory.getZoneRelModule(); 
				}
			}
		}else{
			module = modBean.getModule(importProcessContext.getModule().getName());
		}
		
		
		if (ImportAPI.isAssetBaseModule(importProcessContext.getModule()) && !(importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue())) {
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			fields.add(modBean.getField("name", "resource"));
			Long siteId = importProcessContext.getSiteId();
			SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
					.beanClass(ReadingContext.class).moduleName(module.getName()).select(fields)
					.table(module.getTableName()).andCondition(
							CriteriaAPI.getCondition("Resources.NAME", "name", nameQueryString, StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition("Resources.SITE_ID", "siteId", siteId.toString(), StringOperators.IS));
			List<ReadingContext> readingContext = selectBuilder.get();
			if (readingContext != null && readingContext.size() > 0) {
				for (ReadingContext rContext : readingContext) {
					String assetName = (String) rContext.getData().get("name");
					groupedContext.get(assetName).get(0)
							.setError_resolved(ImportProcessContext.ImportLogErrorStatus.FOUND_IN_DB.getValue());
				}
			}
		}
		context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
		
		return false;
	}

}
