package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.RelationshipOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public enum FacilioRelationshipFunctions implements FacilioWorkflowFunctionInterface {

	GET_RELATIONSHIPS(1,"getRelationships") {
		//relationship().getRelationships(<modulename>)
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			String moduleName = (String)objects[0];
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<RelationRequestContext> relations = RelationUtil.getAllRelations(modBean.getModule(moduleName));
			if(CollectionUtils.isEmpty(relations)) {
				return null;
			}
			return FieldUtil.getAsJSONArray(relations, RelationRequestContext.class);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_RELATED_RECORDS(2,"getRelatedRecords") {
		//relationship().getRelatedRecords(<tomodule>,<reverseRelationLinkName>,<parentid>)
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			String moduleName = (String)objects[0];
			String relationLinkName = (String)objects[1];
			Long parentId = Long.parseLong(objects[2].toString());
			int page = 1;
			int perPage = WorkflowV2Util.SELECT_DEFAULT_LIMIT;
			if(objects.length > 3) {
				page = Integer.parseInt(objects[3].toString());
				perPage = Integer.parseInt(objects[4].toString());
			}

			Criteria serverCriteria = new Criteria();
			serverCriteria.addAndCondition(CriteriaAPI.getCondition(relationLinkName, String.valueOf(parentId), RelationshipOperator.CONTAINS_RELATION));

			FacilioContext listContext = V3Util.fetchList(moduleName, true, null, null, false, null, null,
							null, null, page, perPage, false, null, serverCriteria);
			return Constants.getJsonRecordMap(listContext).get(moduleName);
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	ASSOCIATE(3,"associate") {
		//relationship().associate(<frommodulename>, <forwardRelationLinkName>, <parentid>, <{tomodulename:[ids]}>)
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			String moduleName = (String)objects[0];
			String relationLinkName = (String)objects[1];
			Long parentId = Long.parseLong(objects[2].toString());
			Map<String,Object> relationValue = (HashMap<String,Object>) objects[3];

			Map<String, List<Object>> queryParameters = new HashMap<>();
			queryParameters.put("relationName", new ArrayList(){{add(relationLinkName);}});
			queryParameters.put("parentId", new ArrayList(){{add(parentId);}});

			JSONObject resultData = RelationshipDataUtil.associateRelation(moduleName, FieldUtil.getAsJSON(relationValue), queryParameters, null);
			JSONObject result = new JSONObject();
			result.put("result", "Associated successfully");
			result.put("data", resultData);
			return result;
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	DISSOCIATE(4,"dissociate") {
		//relationship().dissociate(<frommodulename>, <forwardRelationLinkName>, <parentid>, <{tomodulename:[ids]}>)
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			String moduleName = (String)objects[0];
			String relationLinkName = (String)objects[1];
			Long parentId = Long.parseLong(objects[2].toString());
			Map<String,Object> relationValue = (HashMap<String,Object>) objects[3];

			Map<String, List<Object>> queryParameters = new HashMap<>();
			queryParameters.put("relationName", new ArrayList(){{add(relationLinkName);}});
			queryParameters.put("parentId", new ArrayList(){{add(parentId);}});

			JSONObject resultData = RelationshipDataUtil.dissociateRelation(moduleName, FieldUtil.getAsJSON(relationValue), queryParameters, null);

			JSONObject result = new JSONObject();
			result.put("result", "Dissociated successfully");
			result.put("data", resultData);
			return result;
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "relationship";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.RELATIONSHIP;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	FacilioRelationshipFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	
	public static Map<String, FacilioRelationshipFunctions> getAllFunctions() {
		return ASSET_FUNCTIONS;
	}
	public static FacilioRelationshipFunctions getFacilioRelationshipFunction(String functionName) {
		return ASSET_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioRelationshipFunctions> ASSET_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioRelationshipFunctions> initTypeMap() {
		Map<String, FacilioRelationshipFunctions> typeMap = new HashMap<>();
		for(FacilioRelationshipFunctions type : FacilioRelationshipFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
