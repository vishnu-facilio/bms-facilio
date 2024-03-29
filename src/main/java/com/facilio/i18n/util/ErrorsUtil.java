package com.facilio.i18n.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fsm.exception.FSMException;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.*;

@Log4j
public class ErrorsUtil {

    private static final String BASE_PATH = FacilioUtil.normalizePath("conf/translations/error");

    private static final String DEFAULT_LANG = "en";

    private static final Map<String, ResourceBundle> PROPERTIES = new HashMap<>();

    public static ResourceBundle getBundle() {
        //String lang = AccountUtil.getCurrentOrg().getLanguage(); Getting Language by user instead of organization
        String lang = Objects.requireNonNull(AccountUtil.getCurrentAccount()).getUser().getLanguage();
        if (lang == null) {
            lang = DEFAULT_LANG;
        }
        if (!PROPERTIES.containsKey(lang)) {
            Locale locale = new Locale(lang); // Add country code also if needed later
            ResourceBundle bundle = ResourceBundle.getBundle(BASE_PATH, locale);
            PROPERTIES.put(lang, bundle);
        }
        return PROPERTIES.get(lang);
    }

    public static String getString(String key, String... args) {
        ResourceBundle bundle = getBundle();
        String val = bundle.getString(key);
        if (val != null && args != null && args.length > 0) {
            val = MessageFormat.format(val, args);
        }
        return val;
    }

    public static JSONObject getFSMExceptionAsJson(FSMException fsmException) {
        String localizedTitle = getString(fsmException.getFsmErrorCode().name() + ".TITLE");
        if (StringUtils.isEmpty(localizedTitle)) {
            localizedTitle = fsmException.getFsmErrorCode().getTitle();
        }
        String localizedMessage = getString(fsmException.getFsmErrorCode().name() + ".MESSAGE", fsmException.getMessageParams());
        if (StringUtils.isEmpty(localizedMessage)) {
            localizedMessage = MessageFormat.format(fsmException.getMessage(), fsmException.getMessageParams());
        }

        JSONObject errorData = new JSONObject();
        errorData.put("errorCode", fsmException.getFsmErrorCode().name());
        errorData.put("errorTitle", localizedTitle);
        errorData.put("errorMessage", localizedMessage);
        errorData.put("errorSeverity", fsmException.getFsmErrorCode().getSeverity().name());
        if (fsmException.getRelatedData() != null) {
            errorData.put("errorRelatedData", fsmException.getRelatedData());
        }

        if (fsmException.getAdditionalExceptions() != null) {
            JSONArray additionalErrors = new JSONArray();
            for (FSMException additionalExp : fsmException.getAdditionalExceptions()) {
                String localizedAdditionalTitle = getString(additionalExp.getFsmErrorCode().name() + ".TITLE");
                if (StringUtils.isEmpty(localizedAdditionalTitle)) {
                    localizedAdditionalTitle = additionalExp.getFsmErrorCode().getTitle();
                }
                String localizedAdditionalMessage = getString(additionalExp.getFsmErrorCode().name() + ".MESSAGE", additionalExp.getMessageParams());
                if (StringUtils.isEmpty(localizedAdditionalMessage)) {
                    localizedAdditionalMessage = MessageFormat.format(additionalExp.getMessage(), additionalExp.getMessageParams());
                }

                JSONObject additionalErrorData = new JSONObject();
                additionalErrorData.put("errorCode", additionalExp.getFsmErrorCode().name());
                additionalErrorData.put("errorTitle", localizedAdditionalTitle);
                additionalErrorData.put("errorMessage", localizedAdditionalMessage);
                additionalErrorData.put("errorSeverity", additionalExp.getFsmErrorCode().getSeverity().name());
                if (additionalExp.getRelatedData() != null) {
                    additionalErrorData.put("errorRelatedData", additionalExp.getRelatedData());
                }
                additionalErrors.add(additionalErrorData);
            }
            errorData.put("additionalErrors", additionalErrors);
        }
        return errorData;
    }
}