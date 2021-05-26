package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchCustomMultiRecordFieldsCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        List<SupplementRecord> supplementRecords = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if(CollectionUtils.isEmpty(supplementRecords)) {
            supplementRecords = new ArrayList<>();
        }
        if(CollectionUtils.isNotEmpty(fields)) {
            for(FacilioField f : fields) {
                if(!f.isDefault() && f.getDataTypeEnum().isMultiRecord()) {
                    supplementRecords.add((SupplementRecord) f);
                }
            }
            if(CollectionUtils.isNotEmpty(supplementRecords)) {
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementRecords);
            }
        }
        return false;
    }
}
