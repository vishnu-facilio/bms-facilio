package com.facilio.bmsconsoleV3.commands.service;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.context.V3ServiceVendorContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateStatusCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3ServiceContext> v3serviceContext = Constants.getRecordList((FacilioContext) context);
        if (v3serviceContext != null) {
            for (V3ServiceContext service : v3serviceContext) {
                service.setSellingPrice((double) Math.round(service.getSellingPrice() * 100) / 100);
                service.setStatus(V3ServiceContext.ServiceStatus.ACTIVE);


            }


        }
        return false;
    }
}