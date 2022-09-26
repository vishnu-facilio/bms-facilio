package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static com.facilio.agentv2.AgentUtilV2.getAgentBean;

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
            List<Point> points = getPointRequest.getPoints();
            if(FacilioCommand.UNSUBSCRIBE == command || FacilioCommand.SUBSCRIBE == command){
                ControllerMessenger.subscribeUnscbscribePoints(points,command);
            }
            else {
                throw new IllegalArgumentException(" command cant be anything other than sub  and unsub");
            }
            if(command!=FacilioCommand.SUBSCRIBE){
                return true;
            }
            else{
                    if(points!=null && !points.isEmpty()){
                        long agentId = points.get(0).getAgentId();
                        long controllerId = points.get(0).getControllerId();
                        Controller controller = AgentConstants.getControllerBean().getControllerFromDb(controllerId);
                        context.put(AgentConstants.POINT_NAMES,mlPointsToTag(points,agentId));
                        context.put(AgentConstants.CONTROLLER,controller);

                    }
            }
        }
        return false;
    }

     public  List<String> mlPointsToTag(List<Point>points,long agentId) throws Exception {
      List<String>pointNames = new ArrayList<>();
      AgentBean agentBean = getAgentBean();
      FacilioAgent agent = agentBean.getAgent(agentId);
      for (Point point :points){
          if (agent.getAgentType()== AgentType.NIAGARA.getKey()){
                pointNames.add(point.getDisplayName());
          }
          else{
                pointNames.add(point.getName());
          }
      }
      return pointNames;
     }


}
