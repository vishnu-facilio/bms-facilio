package com.facilio.agentv2.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
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
	private static final List<Integer> FILTER_INSTANCES = new ArrayList<>();
	private static final  Map<String, FacilioField> BACNET_POINT_MAP = FieldFactory.getAsMap(FieldFactory.getBACnetIPPointFields());
	private static final  Map<String, FacilioField> POINT_MAP = FieldFactory.getAsMap(FieldFactory.getPointFields());
	static {
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_INPUT.ordinal());//0
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_OUTPUT.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.ANALOG_VALUE.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_INPUT.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_OUTPUT.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.BINARY_VALUE.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.CALENDAR.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.COMMAND.ordinal());//7
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_INPUT.ordinal());//13
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_OUTPUT.ordinal());
		FILTER_INSTANCES.add(BACNetUtil.InstanceType.MULTI_STATE_VALUE.ordinal());//19
	}

	private static final String FILETR_JOIN = StringUtils.join(FILTER_INSTANCES, ",");

	private String querySearch;
	private String status;
	private Long deviceId;
	private Integer controllerType;
	private Long controllerId;
	private Long agentId;
	/**
	 * Get the Point count.Based on the Point filter. e.g.UNCONFIGURED..etc.
	 *
	 * @return the count.
	 */
	public String getCount() {
		try {
			setResult(AgentConstants.DATA, getPointCount(PointStatus.valueOf(status)));
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
			setResult(AgentConstants.DATA, getPointsData(PointStatus.valueOf(status)));
			ok();
		} catch (Exception e) {
			LOGGER.error("Exception  occurred while getting points ", e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}

	private List<Map<String, Object>> getPointsData(PointStatus status) throws Exception {
		GetPointRequest point = new GetPointRequest();
		sanityCheck(point);
		pointFilter(status, point);
		point.pagination(constructListContext(new FacilioContext()));
		return point.getPointsData();
	}

	private long getPointCount(PointStatus status) throws Exception {
		GetPointRequest point = new GetPointRequest();
		sanityCheck(point);
		pointFilter(status, point);
		point.count();
		return (long) point.getPointsData().get(0).getOrDefault(AgentConstants.ID, 0L);
	}

	/**
	 * Point filter.
	 *
	 * @param status the enum type value
	 * @param point Object reference
	 */
	private void pointFilter(PointStatus status, GetPointRequest point) {
		if(status == null) {
			return;
		}
		switch (status) {
		case SUBSCRIBED:
			point.filterSubsctibedPoints();
			break;
		case COMMISSIONED:
			point.filterCommissionedPoints();
			break;
		case CONFIGURED:
			point.filterConfigurePoints();
			break;
		case UNCONFIRURED:
			point.filterUnConfigurePoints();
			break;
		default:
			throw new IllegalArgumentException("Point status is not satisfied");
		}
	}

	/**
	 * Sanity check.
	 * checking all the instance variable  is null or empty.
	 * @param point object reference to set value
	 * @throws Exception
	 */
	private void sanityCheck(GetPointRequest point) throws Exception {
		
		if (getControllerType() == null || getControllerId() == null) {
			throw new IllegalArgumentException("Controller type/controllerId cannot be null");
		}
		point.ofType(FacilioControllerType.valueOf(controllerType));
		if(controllerId > 0){
			point.withControllerId(controllerId);
		}
		Criteria criteria = new Criteria();
		if (controllerType == FacilioControllerType.BACNET_IP.asInt()) {
			criteria.addAndCondition(CriteriaAPI.getCondition(BACNET_POINT_MAP.get(AgentConstants.INSTANCE_TYPE),
					FILETR_JOIN, NumberOperators.EQUALS));
			point.withCriteria(criteria);
		}

		if (controllerId == 0 && controllerType == 0) {
			point.withLogicalControllers(getAgentId());
		}
		if (querySearch != null && !querySearch.trim().isEmpty()) {
			point.querySearch(AgentConstants.COL_NAME, querySearch);
		}
	}
	/**
	 * PointStatus.
	 * we are getting Point based on this enum type value only.
	 */
	public enum PointStatus {
		UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED
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
	
	public static boolean isVirtualPointExist(long agentId) throws Exception {
		GetPointsAction req = new GetPointsAction();
		req.setAgentId(agentId);
		req.setControllerType(0);
		req.setControllerId(0L);
		return req.getPointCount(null) > 0;
	}
}
