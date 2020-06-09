package com.facilio.bmsconsoleV3.commands.vendor;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.util.InsuranceAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class AddInsuranceVendorRollupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VendorContext> vendors = recordMap.get(moduleName);

        Map<String, List<? extends ModuleBaseWithCustomFields>> data = (Map<String, List<? extends ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(CollectionUtils.isNotEmpty(vendors)) {
            for(V3VendorContext vendor : vendors) {
                InsuranceContext ins = InsuranceAPI.getInsurancesForVendor(vendor.getId());
                if (ins != null && !vendor.getHasInsurance()) {
                    if (MapUtils.isNotEmpty(data)) {
                        List<VendorContext> insuredVendors = (List<VendorContext>) data.get(FacilioConstants.ContextNames.VENDORS);
                        if (CollectionUtils.isNotEmpty(insuredVendors)) {
                            vendor.setHasInsurance(true);
                        }
                    }

                    vendor.setHasInsurance(true);
                    InsuranceAPI.updateVendorRollUp(vendor.getId());
                }
            }
        }
        return false;
    }
}
