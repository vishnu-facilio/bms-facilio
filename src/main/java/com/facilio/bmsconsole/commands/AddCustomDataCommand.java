package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class AddCustomDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Object payloadObject = (Object) context.get(AgentConstants.DATA);
        if (payloadObject instanceof JSONObject) {
            JSONObject payload = (JSONObject) payloadObject;
            if (payload != null && !payload.isEmpty()) {
                Iterator keyIterator = payload.keySet().iterator();
                while (keyIterator.hasNext()) {
                    String moduleName = (String) keyIterator.next();
                    JSONArray dataArray = (JSONArray) payload.get(moduleName);
                    List<ModuleBaseWithCustomFields> dataList = FieldUtil.getAsBeanListFromJsonArray(dataArray, ModuleBaseWithCustomFields.class);
                    addData(moduleName, dataList);
                }
            }
        } else if (payloadObject instanceof Map) {
            Map payload = (Map) payloadObject;
            if (payload != null && !payload.isEmpty()) {
                Iterator keyIterator = payload.keySet().iterator();
                while (keyIterator.hasNext()) {
                    String moduleName = (String) keyIterator.next();
                    List dataListObj = (List) payload.get(moduleName);
                    List<ModuleBaseWithCustomFields> dataList = FieldUtil.getAsBeanListFromMapList(dataListObj, ModuleBaseWithCustomFields.class);
                    addData(moduleName, dataList);
                }
            }
        }

        return false;
    }

    private void addData(String moduleName, List<ModuleBaseWithCustomFields> dataList) throws Exception {

        FacilioChain chain = TransactionChainFactory.addModuleBulkDataChain();
        FacilioContext newContext = chain.getContext();
        newContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        newContext.put(FacilioConstants.ContextNames.RECORD_LIST, dataList);

        chain.execute();
    }

}
