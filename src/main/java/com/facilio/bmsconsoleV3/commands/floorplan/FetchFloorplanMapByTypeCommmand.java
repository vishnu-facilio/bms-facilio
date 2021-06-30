package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext.FloorPlanType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;


public class FetchFloorplanMapByTypeCommmand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long floorId =  (long) context.get(FacilioConstants.ContextNames.FLOOR);
		Map<Integer, V3IndoorFloorPlanContext> FloorPlanMap = new HashMap<>();
		
		if(floorId > 0) {
		
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
	        FacilioModule floormodule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
	        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
	        List<FacilioField> selectFields = new ArrayList<>();
	        selectFields.add(FieldFactory.getIdField(module));
	        selectFields.add(fieldsAsMap.get("name"));

	        for(FloorPlanType type : FloorPlanType.values()) {
	        	SelectRecordsBuilder<V3IndoorFloorPlanContext> selectRecordsBuilder = new SelectRecordsBuilder<V3IndoorFloorPlanContext>()
		                .moduleName(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN)
		                .select(selectFields)
		                .beanClass(V3IndoorFloorPlanContext.class)
		                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("floor"), String.valueOf(floorId), NumberOperators.EQUALS))
		                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("floorPlanType"), String.valueOf(type.getIndex()), NumberOperators.EQUALS));
	        	if(type == FloorPlanType.WORKSTATION) {
	        		selectRecordsBuilder.innerJoin(floormodule.getTableName())
	    			.on(module.getTableName()+".ID = Floor.INDOOR_FLOORPLAN_ID");
	        	}
				V3IndoorFloorPlanContext floorplans = selectRecordsBuilder.fetchFirst();
				FloorPlanMap.put(type.getIndex(), floorplans);
			}
	        
	        context.put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS, FloorPlanMap);
		}
		
		return false;
	}
	
}