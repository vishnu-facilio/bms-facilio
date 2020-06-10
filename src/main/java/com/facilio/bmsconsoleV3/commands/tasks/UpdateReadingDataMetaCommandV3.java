package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UpdateReadingDataMetaCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ReadingDataMeta> metaList = (List<ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
        if (metaList != null && !metaList.isEmpty()) {
            ReadingDataMeta.ReadingInputType type = (ReadingDataMeta.ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
            List<ReadingDataMeta> toBeUpdatedList = new ArrayList<>();
            for (ReadingDataMeta meta : metaList) {
                if (meta.getInputTypeEnum() != type) {
                    toBeUpdatedList.add(meta);
                }
            }

            if (!toBeUpdatedList.isEmpty()) {
                ReadingsAPI.updateReadingDataMetaInputType(toBeUpdatedList, type, null);
            }
        }
        return false;
    }
}
