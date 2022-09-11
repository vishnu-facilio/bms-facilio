package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class TransferRequestShipmentModule extends BaseModuleConfig{
    public TransferRequestShipmentModule(){
        setModuleName(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> transferRequestShipment = new ArrayList<FacilioView>();
        transferRequestShipment.add(getAllTransferRequestShipmentView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT);
        groupDetails.put("views", transferRequestShipment);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTransferRequestShipmentView() {

        FacilioModule module = ModuleFactory.getTransferRequestShipmentModule();
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Transfer Request Shipment");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
