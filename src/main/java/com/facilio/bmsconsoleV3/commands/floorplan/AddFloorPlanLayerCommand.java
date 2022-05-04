package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanLayerContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFloorPlanLayerCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule floorplanlayermodule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_LAYER);

		List<String> markersJson = new ArrayList<>();
		List<String> zoneJson = new ArrayList<>();

		markersJson.add("{\"id\":\"formated-secondary-label-marker\",\"type\":\"symbol\",\"source\":\"marker\",\"layout\":{\"icon-allow-overlap\":true,\"text-field\":\"{centerLabel}\",\"text-font\":[\"Open Sans Bold\",\"Arial Unicode MS Bold\"],\"text-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],10,0,16,15],\"text-letter-spacing\":0.05,\"text-offset\":[0,0]},\"paint\":{\"text-color\":\"#fafafa\",\"text-halo-color\":\"#25243e\",\"text-halo-width\":1},\"filter\":[\"all\",[\"in\",\"$type\",\"Point\"]]}");
		markersJson.add("{\"id\":\"secondary-label-marker\",\"type\":\"symbol\",\"source\":\"marker\",\"layout\":{\"icon-allow-overlap\":true,\"text-field\":\"{secondaryLabel}\",\"text-font\":[\"Open Sans Semibold\",\"Arial Unicode MS Bold\"],\"text-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],8,0,16,15],\"text-letter-spacing\":0.03,\"text-offset\":[0,-2]},\"paint\":{\"text-color\":\"#25243e\",\"text-halo-width\":0.5,\"text-halo-color\":\"#ffffff\"},\"filter\":[\"all\",[\"in\",\"$type\",\"Point\"]]}");
		markersJson.add("{\"id\":\"marker\",\"type\":\"symbol\",\"source\":\"marker\",\"layout\":{\"icon-image\":\"{markerId}\",\"text-field\":\"{label}\",\"text-allow-overlap\":true,\"text-font\":[\"Open Sans Semibold\",\"Arial Unicode MS Bold\"],\"text-offset\":[0,2],\"text-anchor\":\"top\",\"icon-allow-overlap\":true,\"icon-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],10,0,16,14],\"text-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],10,0,16,10]},\"paint\":{\"icon-opacity\":[\"case\",[\"boolean\",[\"get\",\"active\"],false],1,0.3]},\"filter\":[\"all\",[\"in\",\"$type\",\"Point\"]]}");

		zoneJson.add("{\"id\":\"zone\",\"type\":\"fill-extrusion\",\"source\":\"spaceZone\",\"paint\":{\"fill-extrusion-color\":[\"get\",\"zoneBackgroundColor\"],\"fill-extrusion-height\":0,\"fill-extrusion-base\":0,\"fill-extrusion-opacity\":0.3},\"filter\":[\"all\",[\"in\",\"$type\",\"Polygon\"]]}");
		zoneJson.add("{\"id\":\"space-text-zone\",\"type\":\"symbol\",\"source\":\"spaceZone\",\"layout\":{\"icon-allow-overlap\":true,\"text-field\":\"{label}\",\"text-font\":[\"Open Sans Bold\",\"Arial Unicode MS Bold\"],\"text-max-width\":15,\"text-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],10,0,16,14],\"text-transform\":\"uppercase\",\"text-letter-spacing\":0.03,\"text-offset\":[0,0]},\"paint\":{\"text-color\":\"#324056\",\"text-halo-color\":\"#ffffff\",\"text-halo-width\":0.5},\"filter\":[\"all\",[\"in\",\"$type\",\"Polygon\"]]}");
		zoneJson.add("{\"id\":\"space-secondary-text-zone\",\"type\":\"symbol\",\"source\":\"spaceZone\",\"layout\":{\"icon-allow-overlap\":true,\"text-field\":\"{secondaryLabel}\",\"text-font\":[\"Open Sans Semibold\",\"Arial Unicode MS Bold\"],\"text-max-width\":15,\"text-size\":[\"interpolate\",[\"linear\"],[\"zoom\"],10,0,16,15],\"text-transform\":\"uppercase\",\"text-letter-spacing\":0.03,\"text-offset\":[\"interpolate\",[\"exponential\",0.5],[\"zoom\"],17,[\"literal\",[0,1.2]],18,[\"literal\",[0,1.2]],19,[\"literal\",[0,1.3]],20,[\"literal\",[0,1.5]]]},\"paint\":{\"text-color\":\"#324056\",\"text-halo-color\":\"#ffffff\",\"text-halo-width\":0.5},\"filter\":[\"all\",[\"in\",\"$type\",\"Polygon\"]]}");

		List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);

		if (CollectionUtils.isNotEmpty((floorplans))) {
			V3IndoorFloorPlanContext floorplan = floorplans.get(0);

			List<V3IndoorFloorPlanLayerContext> layers = new ArrayList<>();

			int index = 1;

			for (String layer : markersJson) {
				System.out.println(layer);
				V3IndoorFloorPlanLayerContext fplayer = new V3IndoorFloorPlanLayerContext();
//				fplayer.setIndoorfloorplanid(floorplan.getId());
				fplayer.setIndoorfloorplan(floorplan);
				fplayer.setLayertype(1);
				fplayer.setLayer(layer);
				fplayer.setOrder(index);
				layers.add(fplayer);
				index++;
			}

			for (String layer : zoneJson) {
				System.out.println(layer);
				V3IndoorFloorPlanLayerContext fplayer = new V3IndoorFloorPlanLayerContext();
//				fplayer.setIndoorfloorplanid(floorplan.getId());
				fplayer.setIndoorfloorplan(floorplan);
				fplayer.setLayertype(2);
				fplayer.setLayer(layer);
				fplayer.setOrder(index);
				layers.add(fplayer);
				index++;
			}
			List<FacilioField> fields = modBean.getAllFields("floorplanlayer");

			V3RecordAPI.addRecord(false, layers, floorplanlayermodule, fields);
		}



		return false;
	}
}