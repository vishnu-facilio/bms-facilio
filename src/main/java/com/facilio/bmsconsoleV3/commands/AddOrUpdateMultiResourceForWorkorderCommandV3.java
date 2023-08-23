package com.facilio.bmsconsoleV3.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MultiResourceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.multiresource.util.MultiResourceUtil;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class AddOrUpdateMultiResourceForWorkorderCommandV3 extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		try {
			ModuleBean modbean = Constants.getModBean();
			FacilioModule routeModule = modbean.getModule(FacilioConstants.Routes.NAME);
			FacilioModule workOrderModule = modbean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			
			V3WorkOrderContext oldRecord = null;
			List<V3WorkOrderContext> workOrderList = Constants.getRecordList((FacilioContext) context);
			Map<Long,V3WorkOrderContext> oldRecordMap = Constants.getOldRecordMap((FacilioContext) context);
	
			if(CollectionUtils.isEmpty(workOrderList)){
				return false;
			}

			for(V3WorkOrderContext workOrder : workOrderList) {
				if(MapUtils.isNotEmpty(oldRecordMap)) {
					 oldRecord = oldRecordMap.get(workOrder.getId());
					 if(workOrder.getRoute()!=null && workOrder.getRoute().getId()!=-1 && oldRecord!=null && oldRecord.getRoute()!=null && workOrder.getRoute().getId() == oldRecord.getRoute().getId()) {
						 return false;
					 }
				}
				if(workOrder.getRoute()!=null && workOrder.getRoute().getId()!=-1) {
					List<MultiResourceContext> multiResourceList= MultiResourceUtil.getMultiResource(routeModule.getModuleId(),workOrder.getRoute().getId());	
		 			if(CollectionUtils.isNotEmpty(multiResourceList)) {
						for(MultiResourceContext multiResource : multiResourceList) {
							multiResource.setParentModuleId(workOrderModule.getModuleId());
							multiResource.setParentRecordId(workOrder.getId());
						}				
					}
		 			MultiResourceUtil.insertMultiResource(multiResourceList);	
				}
			}
		}catch (Exception e) {
			LOGGER.error("Exception In AddOrUpdateMultiResourceForWorkorderCommandV3 -- "+ e.toString());
		}
		return false;
	}
}
