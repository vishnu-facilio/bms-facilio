package com.facilio.mailtracking.handlers;

import com.facilio.mailtracking.OutgoingMailData;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class WorkorderMailHandler implements OutgoingMailData {

    @Override
    public void loadMailData(V3OutgoingMailLogContext record) throws Exception {
      LOGGER.info("I am just logging the WorkorderMailHandler record");
      LOGGER.info(FieldUtil.getAsJSON(record));
    }
}
