package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class V3ResourceAction extends V3Action {

    String qrValue;
    String moduleName;

    List<Long> ids;

    public String getResourceFromQR () throws Exception {

        ResourceContext resource = ResourceAPI.getResource(qrValue,getFilters());
        setData(FacilioConstants.ContextNames.RESOURCE, resource);
        if(resource != null) {
            List<FacilioField> fields = ResourceAPI.getQRSearchFields(resource, moduleName);
            setData(FacilioConstants.ContextNames.FIELDS, fields);
        }
        return SUCCESS;
    }

    public String generateQr () throws Exception {
        FacilioChain updateQrChain = TransactionChainFactory.getUpdateQrChain();
        FacilioContext context=updateQrChain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,ids);
        updateQrChain.execute();
        Map<Long,String> mappedqr =(Map<Long,String>) context.get(FacilioConstants.ContextNames.MAP_QR);
        setData("mappedqr",mappedqr);
        return SUCCESS;
    }
}
