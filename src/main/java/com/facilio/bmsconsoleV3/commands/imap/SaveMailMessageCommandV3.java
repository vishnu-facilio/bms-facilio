package com.facilio.bmsconsoleV3.commands.imap;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveMailMessageCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> messages = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.MESSAGES);
        if(CollectionUtils.isNotEmpty(messages)) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
            FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE);
            FacilioContext createContext = createRecordChain.getContext();
            recordMap.put(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE, messages);
            Constants.setRecordMap(createContext, recordMap);
            createContext.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL));
            createContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            Constants.setModuleName(createContext, FacilioConstants.ContextNames.BASE_MAIL_MESSAGE);
            FacilioModule module = ChainUtil.getModule(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE);
            Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
            createContext.put(Constants.BEAN_CLASS, beanClass);
            createRecordChain.execute();
            Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(createContext);
            if (MapUtils.isNotEmpty(changeSet)) {
                long recordId = changeSet.keySet().stream().findFirst().get();
                context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
            }
        }
        return false;
    }
}
