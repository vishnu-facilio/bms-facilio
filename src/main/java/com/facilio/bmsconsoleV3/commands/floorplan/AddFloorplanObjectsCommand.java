package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.v3.context.Constants;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;

import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanObjectContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.chain.Context;

public class AddFloorplanObjectsCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule floorplanObjectModule = modBean
				.getModule(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
		List<FacilioField> fields = modBean.getAllFields(floorplanObjectModule.getName());

		if (CollectionUtils.isNotEmpty(floorplans)) {

			for (V3IndoorFloorPlanContext floorplan : floorplans) {

				List<V3IndoorFloorPlanObjectContext> floorplanObjects = floorplan.getIndoorfloorplanobjects();

				if (CollectionUtils.isNotEmpty(floorplanObjects)) {

					for (V3IndoorFloorPlanObjectContext floorplanobject : floorplanObjects) {
						Map<String, Object> floorObject = new HashMap<>();
						floorObject.put("id", floorplan.getId());
						floorplanobject.setIndoorfloorplan(
								FieldUtil.getAsBeanFromMap(floorObject, V3IndoorFloorPlanContext.class));
					}
					V3RecordAPI.addRecord(false, floorplanObjects, floorplanObjectModule, fields);
				}

			}

		}

		return false;
	}
}