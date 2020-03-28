package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PublishIOTMessageControlActionCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(PublishIOTMessageControlActionCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {


		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		
		for(ControlActionCommandContext command :commands) {
			
			if(command.getControlActionMode() == ReadingDataMeta.ControlActionMode.SANDBOX.getValue()) {
				continue;
			}
			Map<String, Object> instance = TimeSeriesAPI.getMappedInstance(command.getResource().getId(),command.getFieldId());
			
			if (instance != null && AccountUtil.getCurrentOrg()!= null) {
				
				instance.put("value", command.getValue());
				instance.put("fieldId", command.getFieldId());
				PublishData data = IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IotCommandType.SET);
//				setResult("data", data);		// handle response here
				
			}else {
				if(AccountUtil.getCurrentOrg()!= null){
					Criteria criteria = new Criteria();
					FacilioModule pointModule = ModuleFactory.getPointModule();
					criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getPointFieldIdField(pointModule), String.valueOf(command.getFieldId()),NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getPointResourceIdField(pointModule), String.valueOf(command.getResource().getId()), NumberOperators.EQUALS));
					GetPointRequest getPointRequest = new GetPointRequest()
							.withCriteria(criteria);
					List<Point> points = getPointRequest.getPoints();
					if ((points != null) && ( ! points.isEmpty())){
						Point point = points.get(0);
						point.setValue(command.getValue());
						ControllerMessenger.setValue(point);
					}else {
						LOGGER.info("No point for resource "+command.getResource().getId()+" and fieldId "+command.getFieldId()+" for set vlaue "+command.getValue());
					}
				}else {
					LOGGER.info("Exception occurred current org is null");
				}
			}
		}
		return false;
	}

}
