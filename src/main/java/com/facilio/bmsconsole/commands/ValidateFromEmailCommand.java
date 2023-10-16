package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class ValidateFromEmailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        EMailTemplate template = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
        if (template != null) {
            String fromEmail = template.getFrom();
            Long fromEmailId = template.getFromID();

            if (StringUtils.isNotEmpty(fromEmail) || (fromEmailId != null && fromEmailId > 0)) {
                EmailFromAddress fromEmailAddress = getFromEmailAddress(fromEmail, fromEmailId);
                if (fromEmailAddress == null) {
                    LOGGER.info("Not a valid From EMail address");
                } else {
                    template.setFromID(fromEmailAddress.getId());
                    template.setFrom(fromEmailAddress.getEmail());
                }
            } else {
                EmailFromAddress defaultNotificationFromAddress = MailMessageUtil.getDefaultEmailFromAddress(EmailFromAddress.SourceType.NOTIFICATION);
                if (defaultNotificationFromAddress != null) {
                    template.setFromID(defaultNotificationFromAddress.getId());
                    template.setFrom(defaultNotificationFromAddress.getEmail());
                }
            }
        }
        return false;
    }

    private EmailFromAddress getFromEmailAddress(String fromEmail, Long fromEmailId) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("verificationStatus"), Boolean.TRUE.toString(), BooleanOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), EmailFromAddress.SourceType.NOTIFICATION.getIndex().toString(), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(fromEmail)) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("email"), fromEmail, StringOperators.IS));
        }
        if (fromEmailId != null && fromEmailId > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(fromEmailId), StringOperators.IS));
        }
        List<EmailFromAddress> emailFromAddress = MailMessageUtil.getEmailFromAddress(criteria);
        return CollectionUtils.isNotEmpty(emailFromAddress) ? emailFromAddress.get(0) : null;
    }
}
