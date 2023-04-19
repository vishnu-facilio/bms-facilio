package com.facilio.bmsconsole.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.agentv2.AgentConstants.getPointFields;


public class GetPointsdataCommand extends FacilioCommand {

    Long deviceId;
    Integer controllerType;
    List<Long> controllerIds;
    Long agentId;
    String status;
    Long controllerId;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        status = (String) context.get("status");
        controllerIds = (List<Long>) context.get("controllerIds");
        controllerType = (Integer) context.get("controllerType");
        agentId = (Long) context.get("agentId");
        if(context.containsKey("controllerId")) {
            controllerId = (Long) context.get("controllerId");
        }
        boolean fetchCount = context.containsKey(FacilioConstants.ContextNames.FETCH_COUNT);

        GetPointRequest point = new GetPointRequest();
        if (filterCriteria != null){
            point.withCriteria(filterCriteria);
        }
        sanityCheck(point);
        PointsAPI.pointFilter(PointsAPI.PointStatus.valueOf(status), point);
        if (fetchCount){
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

        if (controllerType == null) {
            throw new IllegalArgumentException("Controller type cannot be null");
        }
        point.ofType(FacilioControllerType.valueOf(controllerType));

        if(!controllerIds.isEmpty()){
            point.withControllerIds(controllerIds);
        }
        if(controllerId != null) {
            if(controllerId >0) {
                point.withControllerId(controllerId);
            }
        }
        if (controllerType == FacilioControllerType.BACNET_IP.asInt()) {
            point.withCriteria(BACNetUtil.getBacnetInstanceTypeCriteria());
        }
        if(agentId != null && controllerIds.isEmpty()){
            point.withAgentId(agentId);
        }
        if(controllerId != null) {
            if (controllerId == 0 && controllerType == 0) {
                point.withLogicalControllers(agentId);
            }
        }

        if (controllerIds.contains(0l) && controllerType == 0) {
            if (controllerIds.size() > 1) {
                throw new IllegalArgumentException("Logical controller can't be selected with other controllers");
            }
            else{
                point.withLogicalControllers(agentId);
            }
        }

    }
}