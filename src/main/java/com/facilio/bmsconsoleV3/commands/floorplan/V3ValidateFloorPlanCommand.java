package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;

public class V3ValidateFloorPlanCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		HashMap recordMap = (HashMap)context.get("recordMap");
		
		List<V3IndoorFloorPlanContext> floorplans = (List<V3IndoorFloorPlanContext>) recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
		
		if (floorplans != null && !floorplans.isEmpty()) {
			for (V3IndoorFloorPlanContext floorplan : floorplans) {
				if(floorplan.getFloor() !=null && floorplan.getFloor().getId() > 0 && floorplan.getFloorPlanType() != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
			        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
			        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

			        SelectRecordsBuilder<V3IndoorFloorPlanContext> selectRecordsBuilder = new SelectRecordsBuilder<V3IndoorFloorPlanContext>()
			                .moduleName(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN)
			                .select(fields)
			                .beanClass(V3IndoorFloorPlanContext.class)
			                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("floor"), String.valueOf(floorplan.getFloor().getId()), NumberOperators.EQUALS))
			                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("floorPlanType"), String.valueOf(floorplan.getFloorPlanType()), NumberOperators.EQUALS));

			        List<V3IndoorFloorPlanContext> duplicates = selectRecordsBuilder.get();

			        if (!CollectionUtils.isEmpty(duplicates)) {
			            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Floor plan already exists for the given floor and plan type");
			        }
				} else {
					throw new RESTException(ErrorCode.VALIDATION_ERROR, "Floor and plan type details are mandatory for creation");
				}
			}
		}
		return false;
		
	}

}
