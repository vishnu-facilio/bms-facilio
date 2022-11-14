package com.facilio.bmsconsoleV3.actions.autocadfileimport;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.autocadfileimport.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

public class AutoCadFileImportAction extends V3Action{
    @Setter
    @Getter
    AutoCadFileImportContext autocadfiledata;

    @Setter
    @Getter
    long id;

    public String createautocad() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAutocadImportAppChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.AUTO_CAD_FILE_IMPORT,autocadfiledata);
        chain.execute();

        setData("autocadimport", context.get(FacilioConstants.ContextNames.AUTO_CAD_FILE_IMPORT));


        return V3Action.SUCCESS;
    }




}
