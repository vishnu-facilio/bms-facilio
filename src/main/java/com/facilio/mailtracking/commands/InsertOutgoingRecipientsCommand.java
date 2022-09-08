package com.facilio.mailtracking.commands;

import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.MailStatus;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.mailtracking.context.V3OutgoingRecipientContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class InsertOutgoingRecipientsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3OutgoingMailLogContext mailLogContext = (V3OutgoingMailLogContext) context.get(MailConstants.ContextNames.OUTGOING_MAIL_LOGGER);

        List<V3OutgoingRecipientContext> records = new ArrayList<>();
        records.addAll(this.getRecipientRecords(mailLogContext, mailLogContext.getOriginalTo()));
        records.addAll(this.getRecipientRecords(mailLogContext, mailLogContext.getOriginalCc()));
        records.addAll(this.getRecipientRecords(mailLogContext, mailLogContext.getOriginalBcc()));

        V3Util.throwRestException(records.isEmpty(), ErrorCode.RESOURCE_NOT_FOUND,
                "OG_MAIL_ERROR :: No recipients found in the given outgoing mail record. So stopping here.. " +
                        "\nwith mailJson : "+context.get(MailConstants.Params.MAIL_JSON)
        );

        OutgoingMailAPI.insertV3(MailConstants.ModuleNames.OUTGOING_RECIPIENT_LOGGER, records);
        return false;
    }

    private List<V3OutgoingRecipientContext> getRecipientRecords(V3OutgoingMailLogContext mailLogContext, String emailAddresses) {
        if(StringUtils.isEmpty(emailAddresses)) {
            return new ArrayList<>();
        }
        List<V3OutgoingRecipientContext> records = new ArrayList<>();
        for (String address : FacilioUtil.splitByComma(emailAddresses)) {
            Pair<String, String> emailMeta = MailMessageUtil.getUserNameAndEmailAddress.apply(address);
            V3OutgoingRecipientContext record = new V3OutgoingRecipientContext();
            record.setLogger(mailLogContext);
            record.setName(emailMeta.getValue());
            record.setRecipient(emailMeta.getKey());
            record.setStatus(MailStatus.IN_PROGRESS.getValue());
            records.add(record);
        }
        return records;
    }

}
