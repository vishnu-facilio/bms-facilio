package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.SelectionRecord;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ImportTemplateAction extends FacilioAction {
	ImportTemplateContext importTemplateContext;
	
	public String saveImportTemplate() throws Exception{
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		FacilioModule module = ModuleFactory.getImportTemplateModule();
		List<FacilioField> fields = FieldFactory.getImportTemplateFields();
		insertRecordBuilder.table(module.getTableName()).fields(fields);
		Map<String,Object> props = FieldUtil.getAsProperties(importTemplateContext);
		insertRecordBuilder.addRecord(props);
		insertRecordBuilder.save();	
		setResult("templateId", (Long) props.get("id"));
		return SUCCESS;
	}
	
	public ImportTemplateContext getImportTemplateContext() {
		return importTemplateContext;
	}

	public void setImportTemplateContext(ImportTemplateContext importTemplateContext) {
		this.importTemplateContext = importTemplateContext;
	}

	public String getTemplateNames() throws Exception{
		FacilioModule module = ModuleFactory.getImportTemplateModule();
		List<FacilioField> fields = FieldFactory.getImportTemplateFields();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.table(module.getTableName()).select(fields);
		List<Map<String,Object>> props = selectRecordBuilder.get();
		if(props.isEmpty()) {
			setResult("importTemplateContext", null);
		}
		else {
  			List<ImportTemplateContext> templateList = new ArrayList<>();
			for(int i=0; i<props.size(); i++) {
				Map<String, Object> prop = props.get(i);
				importTemplateContext = FieldUtil.getAsBeanFromMap(prop, ImportTemplateContext.class);
				templateList.add(importTemplateContext);
			}
			setResult("importTemplateContext", templateList);
		}
		
		return SUCCESS;
	}
	public String getNewTemplate() throws Exception {
		importTemplateContext = new ImportTemplateContext();
		setResult("importTemplateContext", importTemplateContext);
		return SUCCESS;
	}
	public ImportTemplateContext fetchTemplate(Long templateID) throws Exception {
		FacilioModule module = ModuleFactory.getImportTemplateModule();
		List<FacilioField> fields = FieldFactory.getImportTemplateFields();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		FacilioField idField = new FacilioField();
		for(FacilioField field: fields) {
			if(field.getName().equals("id")) {
				idField = field;
			}
			else {
				continue;
			}
		}
		selectRecordBuilder.table(module.getTableName()).select(fields).andCondition(CriteriaAPI.getCondition(idField, templateID.toString(), StringOperators.IS));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props.isEmpty()) {
			return importTemplateContext;
		}
		else {
			Map<String,Object> prop = props.get(0);
			importTemplateContext = FieldUtil.getAsBeanFromMap(prop, ImportTemplateContext.class);
			return importTemplateContext;
			
		}
		
	}

}