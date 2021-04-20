package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import java.util.List;

public class FetchPagesFromTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<QAndATemplateContext> templates = Constants.getRecordList((FacilioContext) context);
        QAndAUtil.fetchChildrenFromParent(templates,
                                        FacilioConstants.QAndA.PAGE,
                                        "parent",
                                        PageContext.class,
                                        p -> p.getParent().getId(),
                                        PageContext::setParent,
                                        QAndATemplateContext::setPages);

        return false;
    }
}
