package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class FetchPagesFromResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ResponseContext> responses = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(responses)) {
            List<QAndATemplateContext> templates = responses.stream().map(ResponseContext::getParent).collect(Collectors.toList());
            QAndAUtil.populatePagesInTemplates(templates);
        }
        return false;
    }
}
