package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.templates.PrerequisiteApproversTemplate;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class PMImportDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long jobId = (Long) context.get(ImportAPI.ImportProcessConstants.JOB_ID);

		ImportProcessContext importProcessContext = ImportAPI.getImportProcessContext(jobId);

 		context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
		
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());

		String moduleName =((JSONObject)json.get("moduleInfo")).get("module").toString();
		
		FileStore fs = FacilioFactory.getFileStore();
		
		InputStream is = fs.readFile(importProcessContext.getFileId());
		
		Workbook workbook = WorkbookFactory.create(is);
		
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		int row_no = 0;
		String sheetName = "Job";
		List<ImportRowContext> rows = new ArrayList<>();
	
		List<String> defaultSecNames = new LinkedList<>(Arrays.asList("Task Description","Special Instructions","Procedures"));
		
		Sheet sheet = workbook.getSheet(sheetName);
	
		Iterator<Row> rowItr = sheet.iterator();
		boolean heading = true;
		
		while (rowItr.hasNext()) {
			ImportRowContext rowContext = new ImportRowContext();
			row_no++;

			Row row = rowItr.next();

			if (row.getPhysicalNumberOfCells() <= 0) {
				break;
			}
			if (heading) {
				Iterator<Cell> cellItr = row.cellIterator();
				int cellIndex = 0;
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();
					String cellValue = cell.getStringCellValue();
					headerIndex.put(cellIndex, cellValue);
					cellIndex++;
				}
				heading = false;
				continue;
			}

			HashMap<String, Object> colVal = new HashMap<>();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Iterator<Cell> cellItr = row.cellIterator();
			while (cellItr.hasNext()) {
				Cell cell = cellItr.next();

				String cellName = headerIndex.get(cell.getColumnIndex());
				if (cellName == null || cellName == "") {
					continue;
				}
				Object val = 0.0;
				try {
					CellValue cellValue = evaluator.evaluate(cell);
					val = ImportAPI.getValueFromCell(cell, cellValue);
				} catch(Exception e) {
					throw new ImportParseException(row_no, cellName, e);
				}
				
				colVal.put(cellName, val);
			}
			
			if (colVal.values() == null || colVal.values().isEmpty()) {
				break;
			} else {
				boolean isAllNull = true;
				for (Object value : colVal.values()) {
					if (value != null) {
						isAllNull = false;
						break;
					}
				}
				if (isAllNull) {
					break;
				}
			}
			
			rowContext.setRowNumber(row_no);
			rowContext.setColVal(colVal);
			rows.add(rowContext);
		}
		Map<String,HashMap<String,LinkedList<Integer>>> titles = new HashMap<String,HashMap<String,LinkedList<Integer>>>();
		
		for(ImportRowContext row : rows){
			
			String title = row.getColVal().get(fieldMapping.get(moduleName+"__"+"title")).toString();
			
			String sectionName = "Untitled Section";
			for(String secName:defaultSecNames){
				if(title.contains("-"+secName)){
					title = title.replaceFirst("-"+secName, "");
					sectionName = secName;
					break;
				}
			}
			
			if(!titles.containsKey(title)){
				LinkedList<Integer> rowList = new LinkedList<Integer>();
				rowList.add(row.getRowNumber());
				HashMap<String,LinkedList<Integer>> secRowsMap= new HashMap<String,LinkedList<Integer>>();
				secRowsMap.put(sectionName, rowList);
				titles.put(title,secRowsMap);
			}else{
				HashMap<String,LinkedList<Integer>> secRowsMap = titles.get(title);
				if(secRowsMap.containsKey(sectionName)){
					LinkedList<Integer> rowList = secRowsMap.get(sectionName);
					rowList.add(row.getRowNumber());
					titles.put(title,secRowsMap);		
				}else{
					LinkedList<Integer> rowList = new LinkedList<Integer>();
					rowList.add(row.getRowNumber());
					secRowsMap.put(sectionName, rowList);
					titles.put(title,secRowsMap);
				}
			}
		}
		
		
		JSONObject workOrderJson = new JSONObject();
		JSONObject pmJson = new JSONObject();
		
		FacilioModule module = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> fields =  FieldFactory.getBimImportProcessMappingFields();
		BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(module, fields, importProcessContext.getId());
		
		HashMap<String, Object> bimDefaultValuesMap = BimAPI.getBimDefaultValues(bimImport.getBimId(),"asset");
		
		Long siteId = Long.parseLong(bimDefaultValuesMap.get("site").toString());
		
		for(Entry<String,HashMap<String,LinkedList<Integer>>> en:titles.entrySet()){
			workOrderJson.put("subject", en.getKey());
			workOrderJson.put("description",null);
			workOrderJson.put("siteId",siteId);
			
			pmJson.put("title", en.getKey());
			pmJson.put("siteId",siteId);
			
			HashMap<String,LinkedList<Integer>> secRowsMap = en.getValue();
			JSONArray taskSecJsonArr = new JSONArray();
			int seq=1;
			
			for(Entry<String,LinkedList<Integer>> sec:secRowsMap.entrySet()){
				JSONObject taskSecJson = new JSONObject();
				
				taskSecJson.put("name", sec.getKey());
				
				JSONArray taskJsonArr = new JSONArray();
				List<Integer> rowNos = sec.getValue();
				for(Integer rowNo:rowNos){
					JSONObject taskJson = new JSONObject();
					ImportRowContext row = rows.stream().filter(r->r.getRowNumber()==rowNo).findFirst().get();
					taskJson.put("name", row.getColVal().get(fieldMapping.get(moduleName+"__"+"taskName").toString()));
					taskJson.put("sequence", seq);
					taskJsonArr.add(taskJson);
					seq++;
				}
				
				taskSecJson.put("taskTemplates", taskJsonArr);
				
				taskSecJsonArr.add(taskSecJson);
			}
			String workOrderString = workOrderJson.toJSONString();
			String preventiveMaintenanceString = pmJson.toJSONString();
			String tasksString = taskSecJsonArr.toJSONString();
			
			WorkOrderContext workorder =new WorkOrderContext();
			if(workOrderString != null) {
				JSONObject obj = (JSONObject) parser.parse(workOrderString);
				workorder = FieldUtil.getAsBeanFromJson(obj, WorkOrderContext.class);
				if (workorder != null && MapUtils.isNotEmpty(workorder.getData())) {
					Map<String, Object> data = workorder.getData();
					for (Object value : data.values()) {
						if (value instanceof Map) {
							Object id = ((Map) value).get("id");
							if (id instanceof Number) {
								((Map) value).put("id", ((Number) id).longValue());
							}
						}
					}
				}
			}
			PreventiveMaintenance preventivemaintenance=new PreventiveMaintenance();
			if(preventiveMaintenanceString != null) {
				JSONObject obj = (JSONObject) parser.parse(preventiveMaintenanceString);
				preventivemaintenance = FieldUtil.getAsBeanFromJson(obj, PreventiveMaintenance.class);
			}
			
			List<TaskSectionTemplate> sectionTemplates = new ArrayList();
			if(tasksString != null) {
				JSONArray obj = (JSONArray) parser.parse(tasksString);
				
				List<TaskSectionTemplate> taskSectionContextList = FieldUtil.getAsBeanListFromJsonArray(obj, TaskSectionTemplate.class);
				sectionTemplates.addAll(taskSectionContextList);
			}
			
			
			workorder.setRequester(null);
			List<PMReminder> reminders = new ArrayList<>(); 
			
			if(reminders != null) {
				preventivemaintenance.setReminders(reminders);
			}
			List<PrerequisiteApproversTemplate> prerequisiteApproverTemplates = new ArrayList();
			
			List<File> attachedFiles = new ArrayList();
			List<String> attachedFilesFileName = new ArrayList();
			List<String> attachedFilesContentType = new ArrayList();
			AttachmentType attachmentType = null;
			
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
//			context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
			context.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES, prerequisiteApproverTemplates);
			context.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionTemplates);
			context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
			
			context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, attachedFiles);
	 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, attachedFilesFileName);
	 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, attachedFilesContentType);
	 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, attachmentType);

	 		FacilioChain addTemplate = FacilioChainFactory.getAddNewPreventiveMaintenanceChain();
	 		addTemplate.execute(context);
	 		
		}
		
		return false;
	}

}
