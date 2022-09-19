package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PoLineItemsSerialNumbersModule extends BaseModuleConfig{
    public PoLineItemsSerialNumbersModule(){
        setModuleName(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> poLineItemsSerialNumbers = new ArrayList<FacilioView>();
        poLineItemsSerialNumbers.add(getAllPoLineItemsSerialNumeberView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS);
        groupDetails.put("views", poLineItemsSerialNumbers);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPoLineItemsSerialNumeberView() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getPoLineItemsSerialNumberModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Serial Numbers");
//		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        return allView;
    }
}
