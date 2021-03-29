package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;


public class AddOrUpdateObjectCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);

	
		if (CollectionUtils.isNotEmpty(floorplans)) {

			for (V3IndoorFloorPlanContext floorplan : floorplans) {
					updateZones(floorplan);
					updateMarker(floorplan);
				}

			}


		return false;
	}

	public static void updateZones(V3IndoorFloorPlanContext floorplan) throws Exception {
						
			List<V3MarkerdZonesContext> zones = floorplan.getMarkedZones();
            List<V3MarkerdZonesContext> localzones = V3FloorPlanAPI.getAllZones(floorplan.getId());
                
			if (CollectionUtils.isNotEmpty(zones)) {
				
				if (localzones == null) {
					V3FloorPlanAPI.addZones(floorplan);
				}
				else {
				List<V3MarkerdZonesContext> updateZones = V3FloorPlanAPI.getUpdateZonesList(zones, localzones);
				if (CollectionUtils.isNotEmpty(updateZones)) {
					V3IndoorFloorPlanContext fp = new V3IndoorFloorPlanContext();
					fp.setId(floorplan.getId());
					fp.setMarkedZones(updateZones);
					V3FloorPlanAPI.updateZones(fp);
				}
				List<V3MarkerdZonesContext> addZonesList = V3FloorPlanAPI.getAddZonesList(zones, localzones);
				if (CollectionUtils.isNotEmpty(addZonesList)) {
					V3IndoorFloorPlanContext fp = new V3IndoorFloorPlanContext();
					fp.setId(floorplan.getId());
					fp.setMarkedZones(addZonesList);
					V3FloorPlanAPI.addZones(fp);				}
				}
			
			}

      }

	public static void updateMarker(V3IndoorFloorPlanContext floorplan) throws Exception {
						
			List<V3MarkerContext> markers = floorplan.getMarkers();
            List<V3MarkerContext> localMarkers = V3FloorPlanAPI.getAllMarkers(floorplan.getId());
                
			if (CollectionUtils.isNotEmpty(markers)) {
				
				if (localMarkers == null) {
					V3FloorPlanAPI.addMarkers(floorplan);
				}
				else {
				List<V3MarkerContext> updateMarkers = V3FloorPlanAPI.getUpdateMarkerList(markers, localMarkers);
				if (CollectionUtils.isNotEmpty(updateMarkers)) {
					V3FloorPlanAPI.updateMarkers(updateMarkers);
				}
				List<V3MarkerContext> addMarkersList = V3FloorPlanAPI.getAddMarkerList(markers, localMarkers);
				if (CollectionUtils.isNotEmpty(addMarkersList)) {
					V3IndoorFloorPlanContext fp = new V3IndoorFloorPlanContext();
					fp.setId(floorplan.getId());
					fp.setMarkers(addMarkersList);
					V3FloorPlanAPI.addMarkers(fp);
				}
				}
			
			}

      }
}