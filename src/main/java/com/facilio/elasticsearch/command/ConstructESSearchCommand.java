package com.facilio.elasticsearch.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.util.SyncUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ConstructESSearchCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        if (StringUtils.isNotEmpty(search)) {
            List<FacilioModule> modulesToSearch = SyncUtil.getModulesToSearch();
            if (CollectionUtils.isNotEmpty(modulesToSearch)) {

            }
        }
        return false;
    }
}
