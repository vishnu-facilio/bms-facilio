package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class GetCommissioningPointsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long controllerId = (long) context.get(ContextNames.CONTROLLER_ID);
		boolean fetchMapped = (boolean) context.get(ContextNames.FETCH_MAPPED);
		FacilioControllerType controllerType = (FacilioControllerType) context.get("controllerType");
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
		List<FacilioField>fields =  (pointModule == null ? FieldFactory.getPointFields() : moduleBean.getAllFields(AgentConstants.POINT));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GetPointRequest getPointRequest = new GetPointRequest()
				.filterConfigurePoints()
				.withControllerId(controllerId)
				.ofType(controllerType)
				.pagination((FacilioContext) context)
				.orderBy(fieldMap.get(AgentConstants.NAME).getCompleteColumnName())
				;
		
		if (fetchMapped) {
			getPointRequest.filterCommissionedPoints();
		}
		else {
			getPointRequest.filterUnMappedPoints();
		}
		
		
		List<Point> points = getPointRequest.getPoints();
		if (CollectionUtils.isNotEmpty(points)) {
			Set<Long> resourceIds = new HashSet<>();
			Set<Long> fieldIds = new HashSet<>(); 
			for(Point point: points) {
				if (point.getResourceId() != null && point.getResourceId()  > 0) {
					resourceIds.add(point.getResourceId());
				}
				if (point.getFieldId() != null && point.getFieldId()  > 0) {
					fieldIds.add(point.getFieldId());
				}
			}
			
			if (!resourceIds.isEmpty()) {
				Map<Long, String> resources = CommissioningApi.getResources(resourceIds);
				context.put(ContextNames.RESOURCE_LIST, resources);
			}
			if (!fieldIds.isEmpty()) {
				Map<Long, Map<String, Object>> readingFieldMap = CommissioningApi.getFields(fieldIds);
				context.put(ContextNames.FIELDS, readingFieldMap);
			}
			context.put("points", points);
		}
		
		return false;
	}

}
