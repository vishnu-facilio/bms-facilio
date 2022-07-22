package com.facilio.bmsconsole.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class GetPointsdataCommand extends FacilioCommand {

    Long deviceId;
    Integer controllerType;
    Long controllerId;
    Long agentId;
    String status;
    private static final  Map<String, FacilioField> BACNET_POINT_MAP = FieldFactory.getAsMap(FieldFactory.getBACnetIPPointFields());
    private static final List<Integer> FILTER_INSTANCES = new ArrayList<>();
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

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        status = (String) context.get("status");
        controllerId = (Long) context.get("controllerId");
        controllerType = (Integer) context.get("controllerType");
        agentId = (Long) context.get("agentId");

        GetPointRequest point = new GetPointRequest();
        if (filterCriteria != null){
            point.withCriteria(filterCriteria);
        }
        sanityCheck(point);
        pointFilter(PointStatus.valueOf(status), point);

        if (context.containsKey(FacilioConstants.ContextNames.FETCH_COUNT)){
            Long pointsCount = getPointCount(point);
            context.put("pointsCount",pointsCount);
            return false;
        }

        List<Map<String,Object>> data = getPointsData(point, (FacilioContext) context);
        context.put("data",data);
        return false;
    }

    private List<Map<String, Object>> getPointsData(GetPointRequest point,FacilioContext context) throws Exception {
        point.pagination(context);
        return point.getPointsData();
    }
    private long getPointCount(GetPointRequest point) throws Exception {
        point.count();
        return (long) point.getPointsData().get(0).getOrDefault(AgentConstants.ID, 0L);
    }

    private void sanityCheck(GetPointRequest point) throws Exception {

        if (controllerType == null || controllerId == null) {
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
            point.withLogicalControllers(agentId);
        }
    }
    private void pointFilter(PointStatus status, GetPointRequest point) {
        if(status == null) {
            return;
        }
        switch (status) {
            case SUBSCRIBED:
                point.filterSubscribedPoints();
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
    public enum PointStatus {
        UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED
    }
}