package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class ValidateRelationshipPickListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String relMappingName = (String) context.get(FacilioConstants.ContextNames.RELATIONSHIP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if(relMappingName == null){
            throw new IllegalArgumentException("Relation mapping name cannot be null");
        }

        if(moduleName == null){
            throw new IllegalArgumentException("Module name cannot be null");
        }

        RelationMappingContext relationMappingContext = RelationUtil.getRelationMapping(relMappingName);

        if(relationMappingContext == null){
            throw new IllegalArgumentException("No relation mapping found");
        }

        FacilioModule fromModule = Constants.getModBean().getModule(relationMappingContext.getFromModuleId());
        FacilioModule toModule = Constants.getModBean().getModule(relationMappingContext.getToModuleId());


        if(!moduleName.equals(toModule.getName())){
            throw new IllegalArgumentException("From Module doesn't match");
        }

        context.put(FacilioConstants.ContextNames.MODULE_NAME, fromModule.getName());

        return false;
    }
}
