package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecordsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
        if(CollectionUtils.isNotEmpty(records) && moduleName != null) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
            FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(moduleName);
            FacilioContext createContext = createRecordChain.getContext();
            recordMap.put(moduleName, records);
            Constants.setRecordMap(createContext, recordMap);
            Constants.setModuleName(createContext, moduleName);
            createContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            createContext.put(FacilioConstants.ContextNames.DATA, context.getOrDefault(FacilioConstants.ContextNames.DATA, null));
            FacilioModule module = ChainUtil.getModule(moduleName);
            Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
            createContext.put(Constants.BEAN_CLASS, beanClass);
            createRecordChain.execute();
        }
        return false;
    }
}
