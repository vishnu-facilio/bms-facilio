package com.facilio.bmsconsoleV3.commands.floorplan;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.util.DesksAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDeskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
		List<FacilioField> fields = modBean.getAllFields(deskModule.getName());

	
		if (CollectionUtils.isNotEmpty(floorplans)) {

			for (V3IndoorFloorPlanContext floorplan : floorplans) {

				List<V3MarkerContext> markers = floorplan.getMarkers();

				if (CollectionUtils.isNotEmpty(markers)) {
       			List<V3DeskContext> desksprop = new ArrayList<V3DeskContext>();
				
					for (V3MarkerContext marker : markers) {
						Map<String, Object> floorObject = new HashMap<>();
						floorObject.put("id", floorplan.getId());
						marker.setIndoorfloorplan(
						FieldUtil.getAsBeanFromMap(floorObject, V3IndoorFloorPlanContext.class));
						
						V3DeskContext desk = marker.getDesk();

						
						if (desk != null) {
							System.out.println("desksprop" + desk.getDeskCode());
							if (desk.getId() < 0) {
								desksprop.add(desk);
							}
							else if (desk.getId() > 0){
								if(desk.getDeskType() != 1 && desk.getEmployee() != null) {
									JSONObject moveObj = new JSONObject();
									moveObj.put("employee", desk.getEmployee());
									moveObj.put("from", desk);
									moveObj.put("moveType", 1);
									moveObj.put("siteId", desk.getSiteId());
									moveObj.put("timeOfMove", System.currentTimeMillis());
									FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.MOVES);
									V3Util.createRecord(module, (JSONObject) moveObj);
								}
								Map<Long, List<UpdateChangeSet>> deskChangeSet = V3RecordAPI.updateRecord(desk, deskModule, fields, true);
								if(deskChangeSet != null && !deskChangeSet.isEmpty()) {
								for(Long deskId : deskChangeSet.keySet()) {
									V3DeskContext updatedDesk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, deskId, V3DeskContext.class);
									DesksAPI.AddorDeleteFacilityForDesks(updatedDesk);
								}
								}
							}

						}

					}

				if (CollectionUtils.isNotEmpty(desksprop)) {
					Map<Long, List<UpdateChangeSet>> deskChangeSet = V3RecordAPI.addRecord(false, desksprop, deskModule, fields,true);
					if(deskChangeSet != null && !deskChangeSet.isEmpty()) {
					for(Long deskId : deskChangeSet.keySet()) {
						V3DeskContext desk = (V3DeskContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.DESKS, deskId, V3DeskContext.class);
						
						DesksAPI.AddorDeleteFacilityForDesks(desk);
					}
					}
											
				}
				}

				}

		}


		return false;
	}
	
}