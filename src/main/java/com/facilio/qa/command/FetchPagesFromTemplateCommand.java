package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;

@Log4j
public class FetchPagesFromTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.info("FetchPagesFromTemplateCommand is called");
        List<QAndATemplateContext> templates = Constants.getRecordList((FacilioContext) context);
        QAndAUtil.fetchChildrenFromParent(templates,
                                        FacilioConstants.QAndA.PAGE,
                                        "parent",
                                            "position",
                                        p -> p.getParent().getId(),
                                        PageContext::setParent,
                                        QAndATemplateContext::setPages,
                                        QAndAUtil::setDefaultPropsAsNullToReduceRespSize);
        LOGGER.info(MessageFormat.format("Fetched pages from template : {0}", (CollectionUtils.isEmpty(templates) ? "null" : templates.get(0).getPages())));

        return false;
    }
}
