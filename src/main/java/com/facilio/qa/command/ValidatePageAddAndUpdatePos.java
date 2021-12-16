package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.PositionUtil;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.*;

@Log4j
public class ValidatePageAddAndUpdatePos extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> list = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long, List<PageContext>> templateVsPages = new HashMap<>();
            for (PageContext page : list) {
                Long templateId = page.getParent() == null ? null : page.getParent().getId();
                V3Util.throwRestException(templateId == null, ErrorCode.VALIDATION_ERROR, "Parent template of page cannot be null");
                V3Util.throwRestException(page.getPosition() == null || page.getPosition() <= 0, ErrorCode.VALIDATION_ERROR, "Invalid position for page");
                templateVsPages.computeIfAbsent(templateId, k -> new ArrayList<>()).add(page);
            }

            Map<Long, QAndATemplateContext> templateMap = V3RecordAPI.getRecordsMap(FacilioConstants.QAndA.Q_AND_A_TEMPLATE, templateVsPages.keySet(), QAndATemplateContext.class);
            for (Long templateId : templateVsPages.keySet()) {
                V3Util.throwRestException(templateMap.get(templateId) == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid parent ({0}) specified for page", templateId));
            }
            PositionUtil.computeAndUpdatePosition(FacilioConstants.QAndA.PAGE,
                    "parent",
                    "position",
                    PageContext.class,
                    templateVsPages,
                    PageContext::getPosition,
                    PageContext::setPosition,
                    p -> p.getParent().getId()
            );
        }

        return false;
    }
}
