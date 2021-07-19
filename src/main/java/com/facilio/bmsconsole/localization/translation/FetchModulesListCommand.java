package com.facilio.bmsconsole.localization.translation;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchModulesListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {

        FacilioChain chain = ReadOnlyChainFactory.getModulesList();
        FacilioContext facilioContext = chain.getContext();
        facilioContext.put(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES,true);
        chain.execute();
        Map<String, List<FacilioModule>> modules = (Map<String, List<FacilioModule>>)facilioContext.get(FacilioConstants.ContextNames.MODULE_LIST);
        if(modules != null && !modules.isEmpty()){
            context.put(FacilioConstants.ContextNames.MODULE_LIST,modules);
        }else {
            throw new RuntimeException("Modules are empty while fetching for Translation");
        }
        return false;
    }
}
