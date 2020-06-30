package com.facilio.agentv2.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class SubscribeUnsbscribeCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(FacilioConstants.ContextNames.INSTANCE_INFO,context) && containsCheck(AgentConstants.CONTROLLER_TYPE,context) && containsCheck(AgentConstants.COMMAND,context)){
            List<Map<String,Object>> instances = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.INSTANCE_INFO);
            if(CollectionUtils.isEmpty(instances)){
                throw new Exception("pointIds cant be empty ");
            }
            FacilioCommand command = (FacilioCommand) context.get(AgentConstants.COMMAND);
    		List<Long> ids = new ArrayList<>();
    		for(Map<String, Object> instance: instances) {
    			long id = (long) instance.get("id");
    			ids.add(id);
    			Map<String, Object> newInstance =new HashMap<>();
    			newInstance.put("subscribeStatus", PointEnum.SubscribeStatus.IN_PROGRESS.getIndex());
    			if (instance.get("thresholdJson") != null) {
    				newInstance.put("thresholdJson", instance.get("thresholdJson"));
    			}
    			 PointsAPI.updatePointsSubscribedOrUnsubscribed(Collections.singletonList(id), newInstance,command);
    		}
           
            GetPointRequest getPointRequest = new GetPointRequest().fromIds(ids)
                    .ofType((FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE));
            List<Point> instanceList = getPointRequest.getPoints();
            if(FacilioCommand.UNSUBSCRIBE == command || FacilioCommand.SUBSCRIBE == command){
                ControllerMessenger.subscribeUnscbscribePoints(instanceList,command);
            }
            else {
                throw new IllegalArgumentException(" command cant be anything other than sub  and unsub");
            }
        }
        return false;
    }
}
