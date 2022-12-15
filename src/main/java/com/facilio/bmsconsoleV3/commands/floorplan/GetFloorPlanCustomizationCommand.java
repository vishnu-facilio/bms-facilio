package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.FloorPlanToolTipContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3FloorplanCustomizationContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.google.gson.Gson;
import org.apache.commons.chain.Context;

import java.util.*;

public class GetFloorPlanCustomizationCommand extends FacilioCommand {

    @SuppressWarnings("null")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long floorplanId = (long) context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID);
        List<FacilioModule> modules = V3FloorPlanAPI.getFloorplanMappedModules(context);
        modules.removeAll(Arrays.asList("desks","parkingStall","space","lockers"));
        modules.removeIf(module -> (Arrays.asList("desks","parkingStall","space","lockers").contains( module.getName())));
        context.put("FLOORPLAN_MAPPED_MODULES", modules);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule floorplanModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(floorplanModule);
        Collection<SupplementRecord> floorplansupplements = new ArrayList<>();
        List<FacilioField> floorplanFields = modBean.getAllFields(floorplanModule.getName());
        Map<String, FacilioField> floorplanFieldMap = FieldFactory.getAsMap(floorplanFields);
        floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.FLOOR));
        floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.BUILDING));
        floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION));
        floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION_BOOKING));
        List<Long> floorplanIds = new ArrayList<>();
        floorplanIds.add(floorplanId);

        List<V3IndoorFloorPlanContext> floorplanList = V3RecordAPI.getRecordsListWithSupplements(floorplanModule.getName(), floorplanIds, beanClassName, floorplansupplements);


        V3IndoorFloorPlanContext floorplan = floorplanList.get(0);
        V3FloorplanCustomizationContext settings=floorplan.getCustomization();
        V3FloorplanCustomizationContext bookingSettings=floorplan.getCustomizationBooking();
        //Assignment Setting Update
            settings.setModules(generateModuleJSON(modules,settings.getModules()));
            floorplan.setCustomization(settings);
            floorplan.setCustomizationJSON(new Gson().toJson(settings));
        //Booking Setting Update
            bookingSettings.setModules(generateModuleJSON(modules,bookingSettings.getModules()));
            floorplan.setCustomizationBooking(bookingSettings);
            floorplan.setCustomizationBookingJSON(new Gson().toJson(bookingSettings));

        // ADD floorplan settings to Context
        if (floorplan != null) {
            V3FloorplanCustomizationContext floorplanAssign = floorplan.getCustomization();
            V3FloorplanCustomizationContext floorplanBooking = floorplan.getCustomizationBooking();
            context.put("FLOORPLAN_ASSIGNMENT_CUSTOMIZATION", floorplanAssign);
            context.put("FLOORPLAN_BOOKING_CUSTOMIZATION", floorplanBooking);
            context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, floorplan);
        }
        return false;
    }
    public static Map<String, FloorPlanToolTipContext> generateModuleJSON(List<FacilioModule> modules,Map<String, FloorPlanToolTipContext> moduleSettings) throws Exception {
        for(FacilioModule module: modules)
        {
            if(!moduleSettings.containsKey(module.getName()))
            {
                FloorPlanToolTipContext toolTip = new FloorPlanToolTipContext();
                toolTip.setModuleName(module.getName());
                toolTip.setModuleDisplayName(module.getDisplayName());
                moduleSettings.put(module.getName(),toolTip);
            }
        }
        return moduleSettings;

    }
}
