package com.facilio.v3.commands;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.var;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Log4j
public class CheckContextTampering extends FacilioCommand {
    private String ChainName;
    private String event;
    private String originalModuleName;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Boolean> tamperingContext = Constants.getTamperingContext(context);
        var isModuleNameTampered = tamperingContext.get("moduleName") != null && tamperingContext.get("moduleName");

        if (!isModuleNameTampered) {
            String moduleName = Constants.getModuleName(context);
            if (!originalModuleName.equals(moduleName)) {
                tamperingContext.put("moduleName", true);
                LOGGER.error("[tamper] Event: " + event + " Module name mismatach orig: "+ moduleName +" tampered: " + (moduleName != null ? moduleName: "null"));
            }
        }

        var isRecordMapTampered = tamperingContext.get("recordMap") != null && tamperingContext.get("recordMap");
        if (!isRecordMapTampered) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
            if (MapUtils.isEmpty(recordMap)) {
                LOGGER.error("[tamper] Event: " + event + " record map empty.");
            } else if (!recordMap.containsKey(originalModuleName)){
                LOGGER.error("[tamper] Event: " + event + " does not contain module name.");
            }
        }

        return false;
    }
}
