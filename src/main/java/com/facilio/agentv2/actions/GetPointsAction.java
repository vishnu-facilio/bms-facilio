package com.facilio.agentv2.actions;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.AgentConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class GetPointsAction extends AgentActionV2 {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(GetPointsAction.class.getName());

	private String status;
	private Long deviceId;
	private Integer controllerType;
	private Long controllerId;
//	private Long agentId;
	private String filters;
	/**
	 * Get the Point count.Based on the Point filter. e.g.UNCONFIGURED..etc.
	 *
	 * @return the count.
	 */
	public String getCount() {
		try {

			FacilioChain chain = ReadOnlyChainFactory.getPointsdataCommand();
			FacilioContext context = chain.getContext();
			setFetchCount(true);
			constructListContext(context);
			context.put("status",status);
			context.put("controllerId",controllerId);
			context.put("controllerType",controllerType);
			context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.POINTS);
			chain.execute();
			setResult(AgentConstants.DATA,context.get("pointsCount"));
			ok();
		} catch (Exception e) {
			LOGGER.error("Exception while getting points count ", e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}

	/**
	 * Get the Points Data.Based on the Point filter. e.g.UNCONFIGURED..etc.
	 *
	 * @return the points data.
	 */
	public String getPoints() {
		try {
			FacilioChain chain = ReadOnlyChainFactory.getPointsdataCommand();
			FacilioContext context = chain.getContext();
			constructListContext(context);
			context.put("status",status);
			context.put("controllerId",controllerId);
			context.put("controllerType",controllerType);
			context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.POINTS);
			chain.execute();
			setResult("data",context.get("data"));
			setResult("resourceMap",context.get("resourceMap"));
			setResult("fieldMap",context.get("fieldMap"));
			setResult("unitMap",context.get("unitMap"));
			ok();
		} catch (Exception e) {
			LOGGER.error("Exception  occurred while getting points ", e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}
	//Getting all controllerIds for specific agentId
	private List<Long> getControllerIds(long agentId) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getNewControllerModule();
		fields.add(FieldFactory.getIdField(module));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName()).andCondition(CriteriaAPI.getCondition(
						FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		return props.stream().map(p -> (Long) p.get("id")).collect(Collectors.toList());
	}

}
