package com.facilio.bmsconsoleV3.commands.plannedmaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;

import java.util.*;

public class fillTriggerIdAfterImport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        Integer setting = importProcessContext.getImportSetting();
        ArrayList<LinkedHashMap<String,Object>> pmPlannerList = new ArrayList<>();
        List<PMTriggerV2> trigger = (List<PMTriggerV2>) context.get("trigger");

        if(setting == 1 || setting == 2 || setting == 5){
            pmPlannerList = (ArrayList<LinkedHashMap<String, Object>>) context.get(ImportAPI.ImportProcessConstants.INSERT_RECORDS);
        }
        if(setting == 3 || setting == 4 || setting == 5){
            pmPlannerList.addAll((ArrayList<LinkedHashMap<String, Object>>) context.get(ImportAPI.ImportProcessConstants.UPDATE_RECORDS));
        }
        if(trigger != null) {
            int increment = 0;
            for (LinkedHashMap<String, Object> map : pmPlannerList) {
                map.put("trigger", trigger.get(increment));
                increment++;
            }
        }

            return false;
        }

    }

