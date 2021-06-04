package com.facilio.bmsconsoleV3.signup.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class SignupUtil {
	public static void addNotesAndAttachmentModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule customNotesModule = new FacilioModule();
        customNotesModule.setName(module.getName() + "notes");
        customNotesModule.setDisplayName(module.getDisplayName() + " Notes");
        customNotesModule.setTableName("Notes");
        customNotesModule.setType(FacilioModule.ModuleType.NOTES);
        customNotesModule.setCustom(true);
        modules.add(customNotesModule);


        FacilioModule customAttachmentModule = new FacilioModule();
        customAttachmentModule.setName(module.getName() + "attachments");
        customAttachmentModule.setDisplayName(module.getDisplayName() + " Attachments");
        customAttachmentModule.setTableName("Attachments");
        customAttachmentModule.setCustom(true);
        customAttachmentModule.setType(FacilioModule.ModuleType.ATTACHMENTS);

        modules.add(customAttachmentModule);

        if (modules != null && modules.size() > 0) {
            for (FacilioModule subModule: modules) {
            	AddSubModulesSystemFieldsCommad.addModuleBasedFields(module, subModule);
            }
        }
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    	
	}
}
