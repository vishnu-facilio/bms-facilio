package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


public class CommissioningApi {
	
	public static CommissioningLogContext commissioniongDetails(long id) throws Exception {
		List<CommissioningLogContext> logs = commissioniongList(Collections.singletonList(id));
		if (CollectionUtils.isNotEmpty(logs)) {
			return logs.get(0);
		}
		return null;
	}
	
	public static List<CommissioningLogContext> commissioniongList(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		List<FacilioField> fields = FieldFactory.getCommissioningLogFields();
		if (ids == null || ids.size() > 1) {
			fields.removeIf(field -> field.getName().equals("pointJsonStr") || field.getName().equals("clientMetaStr"));
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.orderBy(fieldMap.get("sysCreatedTime").getColumnName() + " desc");
		if (ids != null) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			List<CommissioningLogContext> logs = FieldUtil.getAsBeanListFromMapList(props, CommissioningLogContext.class);
			List<Long> logIds = new ArrayList<>();
			Set<Long> agentIds = new HashSet<>();
			for(CommissioningLogContext log: logs) {
				logIds.add(log.getId());
				agentIds.add(log.getAgentId());
			}
			
			Map<Long, List<Map<String, Object>>> controllerMap = getCommissionedControllers(logIds);
			List<FacilioAgent> agents = AgentApiV2.getAgents(agentIds);
			Map<Long, FacilioAgent> agentMap = agents.stream().collect(Collectors.toMap(FacilioAgent::getId, Function.identity()));
			
			for(CommissioningLogContext log: logs) {
				log.setControllers(controllerMap.get(log.getId()));
				log.setAgent(agentMap.get(log.getAgentId()));
			}
			
			return logs;
		}
		
		return null;
	}
	
	private static Map<Long, List<Map<String, Object>>> getCommissionedControllers(List<Long> logIds) throws Exception {
		FacilioModule module = ModuleFactory.getCommissioningLogControllerModule();
		List<FacilioField> fields = FieldFactory.getCommissioningLogControllerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<Long, List<Map<String, Object>>> logControllerMap = new HashMap<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("commissioningLogId"), logIds, NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		List<Long> controllerIds = props.stream().map(prop -> (long) prop.get("controllerId")).collect(Collectors.toList());
		Map<Long, Map<String, Object>> controllersMap = ResourceAPI.getResourceMapFromIds(controllerIds, true);
		
		for(Map<String, Object> prop: props) {
			long logId = (long) prop.get("commissioningLogId");
			List<Map<String, Object>> controllers = logControllerMap.get(logId);
			if (controllers == null) {
				controllers = new ArrayList<>();
				logControllerMap.put(logId, controllers);
			}
			controllers.add(controllersMap.get((Long) prop.get("controllerId")));
		}
		return logControllerMap;
	}

}
