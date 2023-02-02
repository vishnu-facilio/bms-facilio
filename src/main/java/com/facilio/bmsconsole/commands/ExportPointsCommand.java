/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.*;

import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
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
import com.facilio.unitconversion.Unit;
import com.facilio.util.FacilioUtil;
import org.apache.kafka.common.protocol.types.Field;


public class ExportPointsCommand extends FacilioCommand {
	
	Map<Long, String> resources = new HashMap<>();
	Map<Long, Map<String, Object>> fields = new HashMap<>();
	Map<Long, String> categories = new HashMap<>();
	Map<Integer, String> unitMap = new HashMap<>();
	boolean isNiagara = false;
	String status;
	List<Long>controllerIds;
	int controllerType = -1;
	Criteria criteria = new Criteria();
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		long agentId = (long) context.get(AgentConstants.AGENT_ID);
		AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
		FacilioAgent agent = agentBean.getAgent(agentId);
		isNiagara = agent.getAgentType() == AgentType.NIAGARA.getKey();
		if(context.containsKey(AgentConstants.CONTROLLERIDS)){
			controllerIds = (List<Long>) context.get(AgentConstants.CONTROLLERIDS);
		}
		if(context.containsKey(ContextNames.FILTER_CRITERIA)) {
			criteria = (Criteria) context.get(ContextNames.FILTER_CRITERIA);
		}
		if(context.containsKey(ContextNames.STATUS)){
			status = (String) context.get(ContextNames.STATUS);
		}
		if(context.containsKey(AgentConstants.CONTROLLER_TYPE)){
			controllerType = (int)context.get(AgentConstants.CONTROLLER_TYPE);
		}
		
		String fileUrl = null;
		
		List<Map<String, Object>> controllers = getControllers(agentId,controllerType ,controllerIds);
		if (controllers != null) {
			fetchCategories();
			File rootFolder = ExportUtil.getTempFolder("Points");
			String rootPath = rootFolder.getPath();
			for (Map<String, Object> controller: controllers) {
				String controllerName = controller.get("displayName") != null ? (String) controller.get("displayName") : (String) controller.get("name");
				FacilioControllerType controllerType = FacilioControllerType.valueOf(FacilioUtil.parseInt(controller.get(AgentConstants.CONTROLLER_TYPE)));
				Map<String, Object> table = getDetails(controller, controllerType);
				if (table != null) {
					ExportUtil.writeToFile(fileFormat, controllerName.replaceAll("/", "_"), table, false, rootPath);
				}
			}
			fileUrl = ExportUtil.getZipFileUrl(rootFolder);
		}
		
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}
	
	private List<Map<String, Object>> 	getControllers(long agentId,int controllerType,List<Long> controllerIds) throws Exception {
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
		if(controllerType !=-1){
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONTROLLER_TYPE),String.valueOf(controllerType),NumberOperators.EQUALS));
		}
		if(CollectionUtils.isNotEmpty(controllerIds)){
			builder.andCondition(CriteriaAPI.getIdCondition(controllerIds, resourceModule));
		}
		return builder.get();
	}
	
	private List<Map<String, Object>> fetchPoints(Map<String, Object> controller, FacilioControllerType controllerType) throws Exception {
		
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
		FacilioField orderBy = isNiagara ? fieldMap.get(AgentConstants.DISPLAY_NAME) : fieldMap.get(AgentConstants.NAME);
		GetPointRequest getPointRequest = new GetPointRequest()
				.ofType(controllerType)
				.orderBy(orderBy.getCompleteColumnName())
				.limit(-1)
				.withControllerId((long) controller.get("id"))
				;
		if(status != null) {
			PointsAPI.pointFilter(PointsAPI.PointStatus.valueOf(status),getPointRequest);
		}
		if(criteria != null && !criteria.isEmpty()){
			getPointRequest.withCriteria(criteria);
		}
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

			String name = isNiagara ? (String) point.get(AgentConstants.DISPLAY_NAME) : (String) point.get(AgentConstants.NAME);
			addRecord("Name", name, i, headers, record);

			addRecord("Controller",point.get(AgentConstants.DEVICE_NAME),i,headers,record);

			if (isBacnet) {
				addRecord("Instance", point.get(AgentConstants.INSTANCE_NUMBER), i, headers, record);
				BACNetUtil.InstanceType instanceType = BACNetUtil.InstanceType.valueOf((Integer) point.get(AgentConstants.INSTANCE_TYPE));
				addRecord("Instance Type", instanceType, i, headers, record);
			}
			else if (isNiagara) {
				addRecord("Link Name", point.get(AgentConstants.NAME), i, headers, record);
			}

			if(point.get(AgentConstants.CONFIGURE_STATUS)==PointEnum.ConfigureStatus.CONFIGURED.getIndex()){
				if(point.containsKey("interval")) {
					addRecord("Interval", point.get(AgentConstants.DATA_INTERVAL), i, headers, record);
				}
				else{
					addRecord("Interval","Agent Interval", i, headers, record);
				}
			}

			if(( point.get(AgentConstants.CONFIGURE_STATUS)==PointEnum.ConfigureStatus.CONFIGURED.getIndex()) ||(status != null && status.equals("COMMISSIONED"))){

				String category = null;
				if (point.get(AgentConstants.ASSET_CATEGORY_ID) != null) {
					long categoryId = (long) point.get(AgentConstants.ASSET_CATEGORY_ID);
					category = categories.get(categoryId);
				}
				addRecord("Category", category, i, headers, record);

				String asset = null;
				if (point.get(AgentConstants.RESOURCE_ID) != null) {
					long resourceId = (long) point.get(AgentConstants.RESOURCE_ID);
					asset = resources.get(resourceId);
				}
				addRecord("Asset", asset, i, headers, record);

				Map<String, Object> field = null;
				String reading = null;
				if (point.get(AgentConstants.FIELD_ID) != null) {
					long fieldId = (long) point.get(AgentConstants.FIELD_ID);
					field = fields.get(fieldId);
					reading = (String) field.get(AgentConstants.NAME);
				}
				addRecord("Reading", reading, i, headers, record);

				String unit = null;
				if (point.get("unitId") != null) {
					int unitId = (int) point.get("unitId");
					unit = unitMap.get(unitId);
				}
				addRecord("Unit", unit, i, headers, record);

				String mappedTime = null;
				if(point.get(AgentConstants.MAPPED_TIME)!=null){
					mappedTime = DateTimeUtil.getFormattedTime((Long) point.get(AgentConstants.MAPPED_TIME));
					addRecord("Mapped Time", mappedTime, i, headers, record);
				}
			}
			String createdTime = null;
			if(point.get(AgentConstants.CREATED_TIME) != null ){
				 createdTime = DateTimeUtil.getFormattedTime((Long) point.get(AgentConstants.CREATED_TIME));
			}
			addRecord("Created Time",createdTime,i,headers,record);

			if( point.get(AgentConstants.CONFIGURE_STATUS)==PointEnum.ConfigureStatus.CONFIGURED.getIndex()){

				String lastRecordedTime = null;
				if(point.containsKey(AgentConstants.LAST_RECORDED_TIME)) {
					lastRecordedTime = DateTimeUtil.getFormattedTime((Long) point.get(AgentConstants.LAST_RECORDED_TIME));
				}
				addRecord("Last Recorded Time", lastRecordedTime, i, headers, record);
				String value = null;
				if (point.containsKey(AgentConstants.LAST_RECORDED_VALUE)) {
					value = (String) point.get(AgentConstants.LAST_RECORDED_VALUE);
				}
				addRecord("Last Recorded Value",value, i, headers, record);
			}
			addRecord("Writable", point.get(AgentConstants.WRITABLE), i, headers, record);

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
