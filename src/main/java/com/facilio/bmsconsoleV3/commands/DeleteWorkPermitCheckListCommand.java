package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteWorkPermitCheckListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context deleteCheckListContext) throws Exception {
        long recordId = (long) deleteCheckListContext.get(FacilioConstants.ContextNames.RECORD_ID);
        long moduleId = (long) deleteCheckListContext.get(FacilioConstants.ContextNames.MODULE_ID);
        FacilioModule checkListModule = Constants.getModBean().getModule(moduleId);
        List<FacilioField> fields = Constants.getModBean().getAllFields(checkListModule.getName());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(checkListModule.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(recordId,checkListModule));
        Map<String,Object> props = new HashMap<>();
        props.put("deleted",true);
        int isUpdated = updateRecordBuilder.update(props);
        return false;
    }
}
