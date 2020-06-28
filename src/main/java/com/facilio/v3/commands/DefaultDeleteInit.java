package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DefaultDeleteInit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> rawInput = Constants.getRawInput(context);
        String moduleName = Constants.getModuleName(context);

        List<Long> recordIds = (List<Long>) rawInput.get(moduleName);
        Constants.setRecordIds(context, recordIds);
        return false;
    }
}
