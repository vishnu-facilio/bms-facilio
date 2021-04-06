package com.facilio.bmsconsole.jobs;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.service.FacilioService;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j
public class DeleteJobRecords extends FacilioJob {


    @Override
    public void execute ( JobContext jc ) throws Exception {
        try {
            long currentTime = System.currentTimeMillis();
            long orgId = jc.getOrgId();
            long deletedTime = DateTimeUtil.addDays(currentTime, -30);
            if(!FacilioProperties.isProduction() && !FacilioProperties.isDevelopment()){
                FacilioService.runAsService(FacilioConstants.Services.TEMP_JOBS, ()-> deleteFiles(deletedTime, orgId));
            }else {
                FacilioService.runAsService(FacilioConstants.Services.JOB_SERVICE, ()-> deleteFiles(deletedTime, orgId));
            }
            LOGGER.info("Job Deletion completed for org : "+orgId + ".  Time taken to delete : "+(System.currentTimeMillis()- currentTime)+" ms.");
        }catch (Exception e){
            LOGGER.error("Exception occurred in DeleteJobRecords  :  ", e);
            CommonCommandUtil.emailException("DeleteRecordsJob","DeleteRecordsJob Failed - jobid -- " + jc.getJobId(), e);
            throw e;
        }
    }

    private void deleteFiles(long deletedTime,long orgId) throws SQLException {
        String deleteSql = "DELETE FROM Jobs WHERE ORGID = ? AND STATUS = ? AND IS_ACTIVE = ? AND ADDED_TIME < ?  ORDER BY JOBID  LIMIT 5000";
        try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setLong(1, orgId);
            pstmt.setLong(2, 3);
            pstmt.setBoolean(3, false);
            pstmt.setLong(4,deletedTime);
            int rowCount = 0;
            while(true){
                int count = pstmt.executeUpdate();
                if(count < 1){
                    break;
                }
                rowCount+=count;
            }
            LOGGER.info("Deleted row count : "+ rowCount);
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
            throw e;
        }
    }
}
