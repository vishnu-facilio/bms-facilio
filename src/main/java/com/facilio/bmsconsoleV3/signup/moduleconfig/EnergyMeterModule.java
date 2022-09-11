package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class EnergyMeterModule extends BaseModuleConfig{
    public EnergyMeterModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.ENERGY_METER);
    }


    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> employee = new ArrayList<FacilioView>();
        employee.add(getAllEnergyMetersView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ENERGY_METER);
        groupDetails.put("views", employee);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllEnergyMetersView() {
        FacilioView allView = new FacilioView();
        allView.setName("energy");
        allView.setDisplayName("All Energy Meters");
        allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));

        return allView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(ModuleFactory.getAssetsModule());

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

}
