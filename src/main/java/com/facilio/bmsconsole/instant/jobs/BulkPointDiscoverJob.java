package com.facilio.bmsconsole.instant.jobs;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j
public class BulkPointDiscoverJob extends InstantJob {

    private static final Long WAIT_TIME_MS = 1000L;

    @Override
    public void execute(FacilioContext facilioContext) throws Exception {
        List<Long> ids =  (ArrayList)facilioContext.get(AgentConstants.RECORD_IDS);
        Objects.requireNonNull(ids);
        FacilioUtil.throwIllegalArgumentException(ids.size()<=0, "Ids can't be empty");
        for(Long id:ids){
            ControllerApiV2.discoverPoint(id);
            Thread.sleep(WAIT_TIME_MS);
        }

    }
}
