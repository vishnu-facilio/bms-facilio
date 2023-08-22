package com.facilio.bmsconsoleV3.commands.imap;

import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

import lombok.extern.log4j.Log4j;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class SaveMailMessageCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> messages = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.MESSAGES);
        if (CollectionUtils.isNotEmpty(messages)) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
            FacilioChain createRecordChain = ChainUtil.getCreateChain(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE);
            FacilioContext createContext = createRecordChain.getContext();
            recordMap.put(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE, messages);

            trimEmailAddressesToMaxLength(messages);

            Constants.setRecordMap(createContext, recordMap);
            createContext.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, context.get(FacilioConstants.ContextNames.SUPPORT_EMAIL));
            createContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            createContext.put(FacilioConstants.ContextNames.REQUEST_EMAIL_ID, context.get(FacilioConstants.ContextNames.REQUEST_EMAIL_ID));
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

    private void trimEmailAddressesToMaxLength(List<ModuleBaseWithCustomFields> messages) {
        try {
            for (int i = 0; i < messages.size(); i++) {
                BaseMailMessageContext message = (BaseMailMessageContext) messages.get(i);
                int maxLength = 999;
                if (message != null) {
                    String originalToAddress = message.getTo();
                    if (originalToAddress != null && !originalToAddress.isEmpty()) {
                        String trimmedToAddress = trimAddressToMaxLength(originalToAddress, maxLength);
                        ((BaseMailMessageContext) messages.get(i)).setTo(trimmedToAddress);
                    }
                    String originalBccAddress = message.getBcc();
                    if (originalBccAddress != null && !originalBccAddress.isEmpty()) {
                        String trimmedBccAddress = trimAddressToMaxLength(originalBccAddress, maxLength);
                        ((BaseMailMessageContext) messages.get(i)).setBcc(trimmedBccAddress);
                    }
                    String originalCcAddress = message.getCc();
                    if (originalCcAddress != null && !originalCcAddress.isEmpty()) {
                        String trimmedCcAddress = trimAddressToMaxLength(originalCcAddress, maxLength);
                        ((BaseMailMessageContext) messages.get(i)).setCc(trimmedCcAddress);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("Exception Occured :: " + e);
        }
    }

    public String trimAddressToMaxLength(String address, int maxLength) {
        LOGGER.info("Original Email Address :: " + address);
        if (address.length() >= maxLength) {
            address = address.substring(0, maxLength);
            int lastIndex = address.lastIndexOf(",");
            if (lastIndex != -1) {
                address = address.substring(0, lastIndex);
                LOGGER.info("Trimmed Email Address :: " + address);
            }
        }
        return address;
    }

}
