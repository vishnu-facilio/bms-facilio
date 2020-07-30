package com.facilio.elasticsearch.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.util.ESUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteDataFromESCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(recordIds)) {
            Map<String, List<Long>> esDeleteData = new HashMap<>();
            esDeleteData.put(moduleName, recordIds);

            ESUtil.deleteData(esDeleteData);
        }
        return false;
    }
}
