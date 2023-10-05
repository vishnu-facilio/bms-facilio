package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.CalendarApi;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.DBConf;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Log4j
public class ControlActionTemplateNightlyJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            List<V3ControlActionTemplateContext> controlActionTemplateContextList = ControlActionAPI.getControlActionTemplateForNightlyJobExecution();
            if(CollectionUtils.isNotEmpty(controlActionTemplateContextList)){
                Long currentTime = System.currentTimeMillis();
                ZoneId zoneId = DBConf.getInstance().getCurrentZoneId();;
                ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), zoneId);
                dateTime = dateTime.plusDays(5);
                for(V3ControlActionTemplateContext v3ControlActionTemplateContext : controlActionTemplateContextList){
                    Long generatedUpToTime = ControlActionAPI.getGeneratedUpToTimeForControlActionTemplate(v3ControlActionTemplateContext.getId());
                    dateTime = dateTime.minusDays(1);
                    Long endTime = dateTime.toEpochSecond()*1000;
                    if(endTime > generatedUpToTime){
                        ZonedDateTime startTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(generatedUpToTime), zoneId);
                        startTime = startTime.plusDays(1);
                        ControlActionAPI.generateControlActionFromTemplateWms(v3ControlActionTemplateContext.getId(),startTime.toEpochSecond()*1000,endTime);
                    }
                }
            }
        }
        catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }
}
