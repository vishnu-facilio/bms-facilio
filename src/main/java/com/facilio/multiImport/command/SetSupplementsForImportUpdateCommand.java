package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SetSupplementsForImportUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);
        if (fields == null) {
            fields = Constants.getModBean().getAllFields(moduleName);
        }
        if(CollectionUtils.isNotEmpty(fields)) {
            List<SupplementRecord> supplements = fields.stream().filter(f -> f.getDataTypeEnum().isRelRecordField())
                    .map(f -> (SupplementRecord) f)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(supplements)) {
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
            }
        }
        return false;
    }
}
