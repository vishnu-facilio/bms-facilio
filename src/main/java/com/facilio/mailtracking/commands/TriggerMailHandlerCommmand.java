package com.facilio.mailtracking.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailData;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class TriggerMailHandlerCommmand extends FacilioCommand {

    private static final String CONF_PATH = FacilioUtil.normalizePath("conf/mail/mailDataConf.yml");
    private static final Map<String, OutgoingMailData> MAIL_DATA_MAP = initMailData();
    private static Map<String, OutgoingMailData> initMailData() {
        Yaml yaml = new Yaml();
        Map<String, Object> json = null;
        try(InputStream inputStream = TriggerMailHandlerCommmand.class.getClassLoader().getResourceAsStream(TriggerMailHandlerCommmand.CONF_PATH)) {
            json = yaml.load(inputStream);
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("OG_MAIL_ERROR :: Error occurred while reading outgoing mail data fetch conf file, msg : {0}",e.getMessage()), e);
        }

        try {
            List<Map<String, Object>> handlers = (List<Map<String, Object>>) json.get("handlers");
            if (CollectionUtils.isNotEmpty(handlers)) {
                Map<String, OutgoingMailData> mailDataMap = new HashMap<>();
                String handler = null;
                for (Map<String, Object> mailMeta : handlers) {
                    try {
                        String moduleName = (String) mailMeta.get("modulename");
                        handler = (String) mailMeta.get("handler");
                        mailDataMap.put(moduleName, (OutgoingMailData) Class.forName(handler).newInstance());
                    }
                    catch (Exception e) {
                        throwRunTimeException(MessageFormat.format("OG_MAIL_ERROR :: Error occurred while creating instance of OutgoingMailData class {0}, msg : {1}", handler, e.getMessage()), e);
                    }
                }
                return Collections.unmodifiableMap(mailDataMap);
            }
            else {
                return Collections.emptyMap();
            }
        }
        catch (Exception e) {
            throwRunTimeException(MessageFormat.format("OG_MAIL_ERROR :: Error occurred while parsing mailData conf file, msg : {0}",e.getMessage()), e);
        }
        return null;
    }

    private static void throwRunTimeException (String logMsg, Throwable e) {
        LOGGER.error(logMsg, e);
        throw new RuntimeException(logMsg, e);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);
        if(mailLogContext == null) {
            return false;
        }
        Long recordModuleId = mailLogContext.getRecordsModuleId();
        if(recordModuleId == null || recordModuleId.equals(0)) {
            return false;
        }
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(recordModuleId);
        if(module == null) {
            return false;
        }
        OutgoingMailData mailData = MAIL_DATA_MAP.get(module.getName());
        if(mailData != null) {
            mailData.loadMailData(mailLogContext);
        }
        return false;
    }

}
