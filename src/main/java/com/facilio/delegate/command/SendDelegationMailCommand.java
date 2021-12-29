package com.facilio.delegate.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.modules.FieldUtil;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendDelegationMailCommand extends FacilioCommand implements Serializable {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        DelegationContext delegationContext = (DelegationContext) context.get(FacilioConstants.ContextNames.DELEGATION_CONTEXT);

        sendMail(delegationContext);
        return false;
    }

    private void sendMail(DelegationContext delegationContext) {
        if (System.currentTimeMillis() > delegationContext.getToTime()) {
            // don't want to send the mails
            return;
        }

        try {
            DefaultTemplate defaultTemplate = TemplateAPI.getDefaultTemplate(DefaultTemplate.DefaultTemplateType.ACTION, 120);
            JSONObject json = defaultTemplate.getOriginalTemplate();

            Map<String, Object> placeHolders = new HashMap<>();
            placeHolders.put("org", AccountUtil.getCurrentOrg());
            placeHolders.putAll(FieldUtil.getAsProperties(delegationContext));
            int delegationType = delegationContext.getDelegationType();
            List<String> responsibilities = new ArrayList<>();
            for (DelegationType type : DelegationType.values()) {
                if ((type.getDelegationValue() & delegationType) == type.getDelegationValue()) {
                    responsibilities.add(type.getValue());
                }
            }
            placeHolders.put("responsibilities", responsibilities);
            placeHolders.put("dUser", placeHolders.get("user"));
            for (Object key : json.keySet()){
                String s = FreeMarkerAPI.processTemplate(json.get(key).toString(), placeHolders);
                json.put(key, s);
            }
            FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(json, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
