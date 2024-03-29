package com.facilio.bmsconsoleV3.commands.insurance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.InsuranceAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3InsuranceContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AssociateVendorToInsuranceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InsuranceContext> insurances = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(insurances)) {
            for(V3InsuranceContext ins : insurances) {
                if(ins.getAddedBy() != null && ins.getAddedBy().getId() > 0 && ins.getVendor() == null) {
                    long pplId = PeopleAPI.getPeopleIdForUser(ins.getAddedBy().getOuid());
                    if(pplId > 0) {
                        V3VendorContactContext people = (V3VendorContactContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, pplId, V3VendorContactContext.class);
                        if(people != null) {
                            ins.setVendor(people.getVendor());
                        }
                    }
                }
                if(ins.getVendor() != null) {
                    InsuranceAPI.updateVendorRollUp(ins.getVendor().getId());
                }
            }
        }
        return false;
    }
}
