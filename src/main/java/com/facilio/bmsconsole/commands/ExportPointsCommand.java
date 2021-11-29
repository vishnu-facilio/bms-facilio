/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.unitconversion.Unit;
import com.facilio.util.FacilioUtil;


public class ExportPointsCommand extends FacilioCommand {
	
	Map<Long, String> resources = new HashMap<>();
	Map<Long, Map<String, Object>> fields = new HashMap<>();
	Map<Long, String> categories = new HashMap<>();
	Map<Integer, String> unitMap = new HashMap<>();
	boolean isNiagara = false;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		long agentId = (long) context.get(AgentConstants.AGENT_ID);
		FacilioAgent agent = AgentApiV2.getAgent(agentId);
		isNiagara = agent.getAgentType() == AgentType.NIAGARA.getKey();
		
		String fileUrl = null;
		
		List<Map<String, Object>> controllers = getControllers(agentId);
		if (controllers != null) {
			fetchCategories();
			File rootFolder = ExportUtil.getTempFolder("Points");
			String rootPath = rootFolder.getPath();
			for (Map<String, Object> controller: controllers) {
				String controllerName = controller.get("displayName") != null ? (String) controller.get("displayName") : (String) controller.get("name");
				FacilioControllerType controllerType = FacilioControllerType.valueOf(FacilioUtil.parseInt(controller.get(AgentConstants.CONTROLLER_TYPE)));
				Map<String, Object> table = getDetails(controller, controllerType);
				if (table != null) {
					ExportUtil.writeToFile(fileFormat, controllerName, table, false, rootPath);
				}
			}
			fileUrl = ExportUtil.getZipFileUrl(rootFolder);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}
	
	private List<Map<String, Object>> getControllers(long agentId) throws Exception {
		FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getControllersField());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(fieldMap.get("id"));
		fields.add(FieldFactory.getNameField(resourceModule));
		fields.add(fieldMap.get(AgentConstants.CONTROLLER_TYPE));
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(controllerModule.getTableName())
				.innerJoin(resourceModule.getTableName())
				.on(controllerModule.getTableName()+".ID="+resourceModule.getTableName()+".ID")
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(resourceModule), String.valueOf(false), BooleanOperators.IS));
				;
		
		return builder.get();
	}
	
	private List<Map<String, Object>> fetchPoints(Map<String, Object> controller, FacilioControllerType controllerType) throws Exception {
		
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
		FacilioField orderBy = isNiagara ? fieldMap.get(AgentConstants.DISPLAY_NAME) : fieldMap.get(AgentConstants.NAME);
		GetPointRequest getPointRequest = new GetPointRequest()
				.filterPointsForCommissioning()
				.ofType(controllerType)
				.orderBy(orderBy.getCompleteColumnName())
				.limit(-1)
				.withControllerId((long) controller.get("id"))
				;
		
		return getPointRequest.getPointsData();
	}
	
	private Map<String,Object> getDetails(Map<String, Object> controller, FacilioControllerType controllerType) throws Exception {
		Set<Long> resourceIds = new HashSet<>();
		Set<Long> fieldIds = new HashSet<>(); 
		
		List<Map<String, Object>> points = fetchPoints(controller, controllerType); 
		if (CollectionUtils.isNotEmpty(points)) {
			for (Map<String, Object> point: points) {
				if (point.get(AgentConstants.RESOURCE_ID) != null) {
					long resourceId = (long) point.get(AgentConstants.RESOURCE_ID);
					if (!resources.containsKey(resourceId)) {
						resourceIds.add(resourceId);
					}
				}
				if (point.get(AgentConstants.FIELD_ID) != null) {
					long fieldId = (long) point.get(AgentConstants.FIELD_ID);
					if (!fields.containsKey(fieldId)) {
						fieldIds.add(fieldId);
					}
				}
				if (point.get("unit") != null && !unitMap.containsKey(point.get("unit"))) {
					int unitId = Integer.parseInt(point.get("unit").toString());
					if (unitId > 0) {
						point.put("unitId", unitId);
						Unit unit = Unit.valueOf(unitId);
						unitMap.put(unitId, unit.getSymbol());
					}
				}
				if(isNiagara && StringUtils.isNotEmpty((String)point.get("displayName"))) { // Temp
					point.put("name", point.get("displayName"));
				}
			}
			
			if (!resourceIds.isEmpty()) {
				resources.putAll(CommissioningApi.getResources(resourceIds));
			}
			if (!fieldIds.isEmpty()) {
				 fields.putAll(CommissioningApi.getFields(fieldIds));
			}
			
			return getTable(controllerType, points);
		}
		return null;
	}
	
	private Map<String,Object> getTable(FacilioControllerType controllerType, List<Map<String, Object>> points) {
		
		List<String> headers = new ArrayList<String>();
		List<Map<String,Object>> records = new ArrayList<>();
		
		boolean isBacnet = controllerType == FacilioControllerType.BACNET_IP || controllerType == FacilioControllerType.BACNET_MSTP;
		
		for (int i = 0; i < points.size(); i++) {
			Map<String, Object> point = points.get(i);
			Map<String,Object> record = new HashMap<>();
			
			String headerKey = "Name";
			String name = point.get("displayName") != null ? (String) point.get("displayName") : (String) point.get("name");
			addRecord(headerKey, name, i, headers, record);
			
			if (isBacnet) {
				headerKey = "Instance";
				addRecord(headerKey, point.get(AgentConstants.INSTANCE_NUMBER), i, headers, record);
				headerKey = "Instance Type";
				addRecord(headerKey, point.get(AgentConstants.INSTANCE_TYPE), i, headers, record);
			}
			
			headerKey = "Category";
			String category = null;
			if (point.get(AgentConstants.ASSET_CATEGORY_ID) != null) {
				long categoryId = (long) point.get(AgentConstants.ASSET_CATEGORY_ID);
				category = categories.get(categoryId);
			}
			addRecord(headerKey, category, i, headers, record);
			
			headerKey = "Asset";
			String asset = null;
			if (point.get(AgentConstants.RESOURCE_ID) != null) {
				long resourceId = (long) point.get(AgentConstants.RESOURCE_ID);
				asset = resources.get(resourceId);
			}
			addRecord(headerKey, asset, i, headers, record);
			
			headerKey = "Reading";
			Map<String, Object> field =  null;
			String reading = null;
			if (point.get(AgentConstants.FIELD_ID) != null) {
				long fieldId = (long) point.get(AgentConstants.FIELD_ID);
				field = fields.get(fieldId);
				reading = (String) field.get("name");
			}
			addRecord(headerKey, reading, i, headers, record);
			
			headerKey = "Unit";
			String unit = null;
			if (point.get("unitId") != null) {
				int unitId = (int) point.get("unitId");
				unit = unitMap.get(unitId);
			}
			addRecord(headerKey, unit, i, headers, record);
			
			records.add(record);
		}
		
		Map<String,Object> table = new HashMap<String, Object>();
		table.put("headers", headers);
		table.put("records", records);
		return table;
	}
	
	private void addRecord(String header, Object value, int index, List<String> headers, Map<String,Object> record) {
		if (index == 0) {
			headers.add(header);
		}
		record.put(header, value);
	}

	private void fetchCategories() throws Exception {
		FacilioChain chain = FacilioChainFactory.getPickListChain();
		Context picklistContext = chain.getContext();
		picklistContext.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.ASSET_CATEGORY);
		chain.execute();
		categories = (Map<Long, String>)picklistContext.get(ContextNames.PICKLIST);
	}
	
	
}
