package com.facilio.workflowv2.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class UserFunctionAPI {
	
	public static WorkflowNamespaceContext getNameSpace(String name) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowNamespaceContext workflowNamespaceContext = null;
		if (props != null && !props.isEmpty()) {
			workflowNamespaceContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowNamespaceContext.class);
		}
		return workflowNamespaceContext;
	}
	
	public static WorkflowUserFunctionContext getUserFunction(Long userFunctionId) throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getIdCondition(userFunctionId, module))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			 
			 WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowUserFunctionContext.class);
			 
			 WorkflowNamespaceContext nameSpace = getNameSpace(workflowContext.getNameSpaceId());
			 workflowContext.setNameSpaceName(nameSpace.getName());
			 
			 return workflowContext;
			
		}
		return null;
	}
	
	public static WorkflowNamespaceContext getNameSpace(Long nameSpaceId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowNamespaceFields())
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(nameSpaceId, ModuleFactory.getWorkflowNamespaceModule()))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			WorkflowNamespaceContext nameSpace =  FieldUtil.getAsBeanFromMap(props.get(0), WorkflowNamespaceContext.class);
			return nameSpace;
		}
		return null;
	}
	
	
	public static List<WorkflowUserFunctionContext> getAllFunctions() throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		
		List<WorkflowUserFunctionContext> functions = new ArrayList<WorkflowUserFunctionContext>(); 
		if (props != null && !props.isEmpty()) {
			 Map<Long, WorkflowNamespaceContext> namespaceMap = getAllNameSpace().stream().collect(Collectors.toMap(WorkflowNamespaceContext::getId, Function.identity()));
			
			for(Map<String, Object> prop :props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				
				WorkflowNamespaceContext nameSpace = namespaceMap.get(workflowContext.getNameSpaceId());
				workflowContext.setNameSpaceName(nameSpace.getName());
				
				functions.add(workflowContext);
			}
		}
		return functions;
	}
	
	public static List<WorkflowNamespaceContext> getAllNameSpace() throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				;

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, WorkflowNamespaceContext.class);
		}
		return null;
	}

	public static WorkflowUserFunctionContext getWorkflowFunction(String nameSpace, String functionName) throws Exception {

		WorkflowNamespaceContext workflowNamespaceContext = getNameSpace(nameSpace); //Shouldn't this be called using bean as well?
		return getWorkflowFunction(workflowNamespaceContext.getId(), functionName);
	}
	
	public static WorkflowContext getWorkflowFunctionFromLinkName(String nameSpace, String functionLinkName) throws Exception {

		WorkflowNamespaceContext workflowNamespaceContext = getNameSpace(nameSpace);
		
		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("linkName"), functionLinkName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), workflowNamespaceContext.getId() + "",NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowUserFunctionContext.class);
		}
		return workflowContext;
		
	}
	
	public static List<FacilioField> getUserFunctionFields() {
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		
		List<FacilioField> wfFields = FieldFactory.getWorkflowFields();
		
		wfFields = wfFields.stream().filter((f) -> !f.getName().equals("id")).collect(Collectors.toList());
		fields.addAll(wfFields);
		
		return fields;
	}

	public static WorkflowUserFunctionContext getWorkflowFunction(Long nameSpaceId, String functionName) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), functionName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), nameSpaceId + "",NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowUserFunctionContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowUserFunctionContext.class);
		}
		return workflowContext;
	}
	
	public static List<WorkflowUserFunctionContext> getWorkflowFunction(List<Long> ids) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getIdCondition(ids, module));

		List<Map<String, Object>> props = selectBuilder.get();

		List<WorkflowUserFunctionContext> functions = new ArrayList<WorkflowUserFunctionContext>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				workflowContext.fillFunctionHeaderFromScript();
				functions.add(workflowContext);
			}
			
		}
		return functions;
	}

	public static Map<Long, WorkflowNamespaceContext> getNameSpacesForIds(List<Long> nameSpaceIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowNamespaceFields())
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(nameSpaceIds, ModuleFactory.getWorkflowNamespaceModule()))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(props)) {
			Map<Long, WorkflowNamespaceContext> nameSpaceIdVsNameSpace = new HashMap<>();
			List<WorkflowNamespaceContext> workflowNamespaceContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowNamespaceContext.class);
			workflowNamespaceContextList.forEach(workflowNamespace -> nameSpaceIdVsNameSpace.put(workflowNamespace.getId(), workflowNamespace));
			return nameSpaceIdVsNameSpace;
		}
		return null;
	}

	public static Map<String, WorkflowNamespaceContext> getNameSpacesForLinkName(Collection<String> linkNames) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("linkName"), StringUtils.join(linkNames, ","), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, WorkflowNamespaceContext> nameSpaceIdVsNameSpace = new HashMap<>();
			List<WorkflowNamespaceContext> workflowNamespaceContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowNamespaceContext.class);
			workflowNamespaceContextList.forEach(workflowNamespace -> nameSpaceIdVsNameSpace.put(workflowNamespace.getLinkName(), workflowNamespace));
			return nameSpaceIdVsNameSpace;
		}
		return null;
	}

	public static Map<Long, WorkflowUserFunctionContext> getFunctionsForIds(Collection<Long> ids, boolean fetchUserObj) throws Exception {
		FacilioModule workflowUserFunctionModule = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(workflowUserFunctionModule.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+workflowUserFunctionModule.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getIdCondition(ids, workflowUserFunctionModule));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(props)) {
			Map<Long, WorkflowUserFunctionContext> functionIdVsFunctionMap = new HashMap<>();
			for(Map<String, Object> prop : props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				workflowContext.fillFunctionHeaderFromScript();

				if (fetchUserObj) {
					setUserObjects(workflowContext);
				}

				functionIdVsFunctionMap.put(workflowContext.getId(), workflowContext);
			}
			return functionIdVsFunctionMap;
		}
		return null;
	}

	public static Map<String, WorkflowUserFunctionContext> getFunctionsForLinkNames(List<String> linkNames, boolean fetchUserObj) throws Exception {
		FacilioModule workflowUserFunctionModule = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(workflowUserFunctionModule.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+workflowUserFunctionModule.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", StringUtils.join(linkNames, ","), StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, WorkflowUserFunctionContext> functionIdVsFunctionMap = new HashMap<>();
			for(Map<String, Object> prop : props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				workflowContext.fillFunctionHeaderFromScript();

				if (fetchUserObj) {
					setUserObjects(workflowContext);
				}

				functionIdVsFunctionMap.put(workflowContext.getLinkName(), workflowContext);
			}
			return functionIdVsFunctionMap;
		}
		return null;
	}

	private static void setUserObjects(WorkflowUserFunctionContext workflowContext) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", AccountUtil.getCurrentOrg().getOrgId());
		if (workflowContext.getSysCreatedBy() > 0) {
			workflowContext.setSysCreatedByObj(userBean.getUser(workflowContext.getSysCreatedBy(), false));
		}
		if (workflowContext.getSysModifiedBy() > 0) {
			workflowContext.setSysModifiedByObj(userBean.getUser(workflowContext.getSysModifiedBy(), false));
		}
	}

	public static List<GenericUpdateRecordBuilder.BatchUpdateContext> constructUpdatePropsForDeleteStatus(FacilioModule module, Collection<Long> recordIds) {
		long currenTime = DateTimeUtil.getCurrenTime();
		long currentUserId = AccountUtil.getCurrentUser().getId();

		FacilioField idField = FieldFactory.getIdField(module);
		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

		for (Long recordId : recordIds) {
			GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
			updateVal.addUpdateValue("deleted", Boolean.TRUE);
			updateVal.addUpdateValue("deletedTime", currenTime);
			updateVal.addUpdateValue("deletedBy", currentUserId);
			updateVal.addWhereValue(idField.getName(), recordId);

			batchUpdateList.add(updateVal);
		}

		return batchUpdateList;
	}
}
