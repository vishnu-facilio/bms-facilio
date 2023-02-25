package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;

import static com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime;

public class KioskVendorCheckOutCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
     Long vendorContactId = (Long) context.get(FacilioConstants.ContextNames.VENDOR_CONTACT);
        V3VendorContactContext vendorContactContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT,vendorContactId,V3VendorContactContext.class);
        FacilioUtil.throwIllegalArgumentException(vendorContactContext== null, "No Vendor Contact Found");
        V3VendorContext vendorContext = vendorContactContext.getVendor();


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOG);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_LOG);

        SelectRecordsBuilder<VisitorLogContextV3> builder = new SelectRecordsBuilder<VisitorLogContextV3>()
                .module(module)
                .beanClass(VisitorLogContextV3.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorContext.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("VISITOR_EMAIL","visitorEmail",vendorContactContext.getEmail(), StringOperators.IS));
        FacilioStatus checkedInStatus = TicketAPI.getStatus(module, "CheckedIn");
        builder.andCondition(CriteriaAPI.getCondition("MODULE_STATE", "moduleState", String.valueOf(checkedInStatus.getId()), NumberOperators.EQUALS));

        VisitorLogContextV3 records = builder.fetchFirst();
        FacilioUtil.throwIllegalArgumentException(records== null, "No Active Check-In");

        updateVisitorLogCheckInCheckoutTime(records,false,System.currentTimeMillis());

        VisitorLogContextV3 updatedVisitorLog = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VISITOR_LOG,records.getId(),VisitorLogContextV3.class);

        context.put(FacilioConstants.ContextNames.VISITOR_LOG,updatedVisitorLog);
        return false;
    }
    }