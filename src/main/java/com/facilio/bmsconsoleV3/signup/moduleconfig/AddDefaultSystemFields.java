package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.List;

public class AddDefaultSystemFields extends SignUpData {
    @Override
    public void addData () throws Exception {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> modules = new ArrayList<>();
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET_ACTIVITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ITEM_ACTIVITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.RECEIPTS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.CONTRACTS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.GATE_PASS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SHIPMENT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET_MOVEMENT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.CONTACT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INSURANCE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDOR_DOCUMENTS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.HAZARD));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PRECAUTION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.USER_NOTIFICATION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TRANSACTION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
            modules.add(modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.MOVES));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_ITEMS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_TOOLS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INVENTORY_RESERVATION));



            FacilioChain addModuleChain = TransactionChainFactory.addDefaultSystemFields();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
    }
}
