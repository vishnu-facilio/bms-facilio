package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;


public class PmPlannerAfterInsertCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(!context.containsKey("isUpdate")){
            return false;
        }
        if(!(Boolean) context.get("isUpdate")){
            return false;
        }
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        JSONObject meta = importProcessContext.getImportJobMetaJson();
        meta.put("Updated",meta.get("Inserted"));
        meta.remove("Inserted");
        importProcessContext.setImportJobMeta(String.valueOf(meta));
        return false;
    }
}
