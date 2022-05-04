package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.bmsconsoleV3.context.floorplan.V3FloorplanCustomizationContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanLayerClientContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanLayerContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
@Log4j
public class getFloorplanLayerCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {

		long floorplanId = (long) context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID);
		List<V3IndoorFloorPlanLayerContext> floorplanLayers = V3FloorPlanAPI.getFloorplanLayer(floorplanId);
		List<V3IndoorFloorPlanLayerClientContext> layersList = new ArrayList<>();

		floorplanLayers.forEach(floorplanlayer -> {
			try {
				String layer  = floorplanlayer.getLayer();
				if (layer != null && !layer.trim().isEmpty()) {
					JSONObject layerJSON = (JSONObject) new JSONParser().parse(layer);
					V3IndoorFloorPlanLayerClientContext layerClientContext = FieldUtil.getAsBeanFromJson(layerJSON, V3IndoorFloorPlanLayerClientContext.class);
					layersList.add(layerClientContext);
				}
			} catch (Exception e) {
				LOGGER.error("Error while converting layer", e);
			}
		});

		context.put(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, layersList);
		return false;
	}
	


}