package com.facilio.bmsconsoleV3.commands.floorplan;

import org.apache.commons.collections4.CollectionUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.util.DesksAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMarkedZonesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule floorplanmarkers = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
		List<FacilioField> fields = modBean.getAllFields(floorplanmarkers.getName());


		FacilioModule zoneModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
		List<FacilioField> zonefields = modBean.getAllFields(zoneModule.getName());
	
		if (CollectionUtils.isNotEmpty(floorplans)) {

			for (V3IndoorFloorPlanContext floorplan : floorplans) {
				List<V3MarkerdZonesContext> zones = floorplan.getMarkedZones();

				if (CollectionUtils.isNotEmpty(zones)) {

					for (V3MarkerdZonesContext zone : zones) {
						Map<String, Object> floorObject = new HashMap<>();
						floorObject.put("id", floorplan.getId());
						zone.setIndoorfloorplan(FieldUtil.getAsBeanFromMap(floorObject, V3IndoorFloorPlanContext.class));
					}
					Map<Long, List<UpdateChangeSet>> zoneChangeSet = V3RecordAPI.addRecord(false, zones, zoneModule, zonefields, true);
					if(zoneChangeSet != null && !zoneChangeSet.isEmpty()) {
					for(Long zoneId : zoneChangeSet.keySet()) {
						V3MarkerdZonesContext changedZone = (V3MarkerdZonesContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES, zoneId, V3MarkerdZonesContext.class);
						
						DesksAPI.AddorDeleteFacilityForSpace(changedZone);
					}
					}
				}
			}
		}
			


		return false;
	}
}