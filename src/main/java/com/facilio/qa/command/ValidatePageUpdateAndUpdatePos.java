package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.PageContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.PositionUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ValidatePageUpdateAndUpdatePos extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PageContext> list = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(list)) {
            Map<Long, PageContext> oldPagesMap = Constants.getOldRecordMap(context, FacilioConstants.QAndA.PAGE);
            List<PageContext> positionToBeUpdated = new ArrayList<>();
            for (PageContext page : list) {
                PageContext oldPage = oldPagesMap.get(page.getId());
                V3Util.throwRestException(page.getParent() != null && page.getParent().getId() > 0 && page.getParent().getId() != oldPage.getParent().getId(), ErrorCode.VALIDATION_ERROR, "Cannot update parent template of page");
                if (page.getPosition() != oldPage.getPosition()) {
                    V3Util.throwRestException(page.getPosition() <= 0, ErrorCode.VALIDATION_ERROR, "Invalid position for page");
                    positionToBeUpdated.add(page);
                }
            }

            if (CollectionUtils.isNotEmpty(positionToBeUpdated)) {
                Map<Long, List<PageContext>> templateVsPages = new HashMap<>();
                for (PageContext page : positionToBeUpdated) {
                    templateVsPages.computeIfAbsent(page.getParent().getId(), k -> new ArrayList<>()).add(page);
                }
                if (!templateVsPages.isEmpty()) {
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
            }
        }
        return false;
    }
}
