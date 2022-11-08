package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanGeoJsonContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanPropertiesContext;
import com.facilio.bmsconsoleV3.util.V3SpaceBookingApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class SetBookingFormIDCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {


        List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_MARKERS");
        List<V3IndoorFloorPlanGeoJsonContext> geoZones = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_ZONES");


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deskmodule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        FacilioModule spacebookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);


        geoMarkers.forEach(marker -> {
            V3IndoorFloorPlanPropertiesContext properties = marker.getProperties();

            if(deskmodule.getModuleId() == properties.getMarkerModuleId()) {
                try {
                    Long FormId = V3SpaceBookingApi.getSpaceCategoryRelationFormId(properties.getSpaceCategoryId(), spacebookingModule.getModuleId());
                    if(FormId != null) {
                        marker.getProperties().setBookingFormId(FormId);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });

        geoZones.forEach(zone -> {
            V3IndoorFloorPlanPropertiesContext properties = zone.getProperties();

            try {
                Long FormId = V3SpaceBookingApi.getSpaceCategoryRelationFormId(properties.getSpaceCategoryId(), spacebookingModule.getModuleId());
                if(FormId != null) {
                    zone.getProperties().setBookingFormId(FormId);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });







        return false;
    }
}
