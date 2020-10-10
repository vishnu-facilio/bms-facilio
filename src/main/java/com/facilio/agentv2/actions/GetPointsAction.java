/**
 * @author arunkumar
 */
package com.facilio.agentv2.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class GetPointsAction extends AgentActionV2 {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(GetPointsAction.class.getName());
	private static final List<Integer> FILTER_INSTANCES = new ArrayList<>();
	private static final  Map<String, FacilioField> BACNET_POINT_MAP = FieldFactory.getAsMap(FieldFactory.getBACnetIPPointFields());

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

	public String getQuerySearch() {
		return querySearch;
	}

	public void setQuerySearch(String querySearch) {
		this.querySearch = querySearch;
	}

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private Long deviceId;

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	private Integer controllerType;
	public Integer getControllerType() {
		return controllerType;
	}

	public void setControllerType(Integer controllerType) {
		this.controllerType = controllerType;
	}

	private Long controllerId;

	public Long getControllerId() {
		return controllerId;
	}

	public void setControllerId(Long controllerId) {
		this.controllerId = controllerId;
	}

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
		try {
			GetPointRequest point = new GetPointRequest();
			sanityCheck(point);
			pointFilter(status, point);
			point.count();
			return (long) point.getPointsData().get(0).getOrDefault(AgentConstants.ID, 0L);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Point filter.
	 *
	 * @param status the enum type value
	 * @param point Object reference
	 * @throws if none of the conditions satisfied, then throws IllegalArgumentException.
	 */
	private void pointFilter(PointStatus status, GetPointRequest point) throws Exception {
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

		if (getControllerType() != null) {
			point.ofType(FacilioControllerType.valueOf(getControllerType()));
			if (getControllerType() == FacilioControllerType.BACNET_IP.asInt()) {
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition(BACNET_POINT_MAP.get(AgentConstants.INSTANCE_TYPE),FILETR_JOIN, NumberOperators.EQUALS));
				point.withCriteria(criteria);
			}
		}
		if (getDeviceId() != null && getDeviceId() > 0) {
			point.withDeviceId(getDeviceId());
		}
		if (StringUtils.isNotEmpty(querySearch)) {
			point.querySearch(AgentConstants.COL_NAME, querySearch);
		}
		if (getControllerId() != null && getControllerId() > 0) {
			point.withControllerId(getControllerId());
		}
	}
	/**
	 * PointStatus.
	 * we are getting Point based on this enum type value only.
	 */
	public enum PointStatus {
		UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED
	}
}
