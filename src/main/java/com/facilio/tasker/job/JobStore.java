package com.facilio.tasker.job;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.server.ServerInfo;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.SchedulerJobConf;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JobStore {

    private static final Logger LOGGER = LogManager.getLogger(JobStore.class.getName());
	public static void addJob(JobContext job) throws Exception {
		if(job != null) {
			
			checkForNull(job);
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("INSERT INTO Jobs (JOBID, ORGID, JOBNAME, TIMEZONE, IS_ACTIVE, TRANSACTION_TIMEOUT, IS_PERIODIC, PERIOD, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, END_EXECUTION_TIME, MAX_EXECUTION, STATUS, JOB_SERVER_ID, ADDED_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setLong(1, job.getJobId());
				
				if(job.getOrgId() != -1) {
					pstmt.setLong(2, job.getOrgId());
				}
				else {
					pstmt.setNull(2, Types.BIGINT);
				}
				
				pstmt.setString(3, job.getJobName());

				if (job.getTimezone() != null && !job.getTimezone().isEmpty()) {
					pstmt.setString(4, job.getTimezone());
				}
				else {
					pstmt.setNull(4, Types.VARCHAR);
				}

				pstmt.setBoolean(5, true);
				
				if(job.getTransactionTimeout() != -1) {
					pstmt.setInt(6, job.getTransactionTimeout());
				}
				else {
                    SchedulerJobConf.Job schedulerJob = FacilioScheduler.getSchedulerJob(job.getJobName());
                    pstmt.setInt(6, schedulerJob.getTransactionTimeout());
				}
				
				pstmt.setBoolean(7, job.isPeriodic());
				
				if(job.getPeriod() != -1) {
					pstmt.setInt(8, job.getPeriod());
				}
				else {
					pstmt.setNull(8, Types.INTEGER);
				}
				
				if(job.getSchedule() != null) {
					pstmt.setString(9, job.getScheduleJson());
				}
				else {
					pstmt.setNull(9, Types.VARCHAR);
				}
				
				pstmt.setLong(10, job.getExecutionTime());
				pstmt.setString(11, job.getExecutorName());
				
				if(job.getEndExecutionTime() != -1) {
					pstmt.setLong(12, job.getEndExecutionTime());
				}
				else {
					pstmt.setNull(12, Types.BIGINT);
				}
				
				if(job.getMaxExecution() != -1) {
					pstmt.setInt(13, job.getMaxExecution());
				}
				else {
					pstmt.setNull(13, Types.INTEGER);
				}

				pstmt.setInt(14, JobConstants.JOB_COMPLETED);
				pstmt.setLong(15, job.getJobServerId());

				if(job.getAddedTime() != -1L){
					pstmt.setLong(16,job.getAddedTime());
				}else {
					pstmt.setNull(16,Types.BIGINT);
				}
				
				if(pstmt.executeUpdate() < 1) {
					throw new Exception("Unable to schedule");
				}
				else {
					System.out.println("Added job : "+job);
				}
			}
			catch(SQLException e) {
				throw e;
			}
			finally {
				DBUtil.closeAll(conn, pstmt, rs);
			}
		}
		else {
			throw new IllegalArgumentException("Job object cannot be null");
		}
	}
	
	private static void checkForNull(JobContext job) {
		if(job.getJobId() == -1) {
			throw new IllegalArgumentException("Job ID cannot be null");
		}

		if(FacilioScheduler.getSchedulerJob(job.getJobName()) == null) {
			throw new IllegalArgumentException("Scheduled Job is not configured");
		}
		
		if(job.getJobName() == null || job.getJobName().isEmpty()) {
			throw new IllegalArgumentException("Job name cannot be null");
		}
		
		if(job.getExecutorName() == null || job.getExecutorName().isEmpty()) {
			throw new IllegalArgumentException("Job executor name cannot be null. Job : "+job.getJobName());
		}
		
		if(job.getExecutionTime() == -1) {
			throw new IllegalArgumentException("Invalid execution time for Job : "+job.getJobName());
		}
		
		if(job.isPeriodic() && job.getPeriod() == -1 && job.getSchedule() == null) {
			throw new IllegalArgumentException("Either period or schedule info should be specified for recurring job : "+job);
		}
	}
	
	public static long updateNextExecutionTimeAndCount(long orgId, long jobId, String jobName, long nextExecutionTime, int executionCount) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set NEXT_EXECUTION_TIME = ?, CURRENT_EXECUTION_COUNT = ?, STATUS = ?, EXECUTION_ERROR_COUNT = ? where ORGID = ? AND JOBID = ? AND JOBNAME = ?");
			
			pstmt.setLong(1, nextExecutionTime);
			pstmt.setInt(2, executionCount);
			pstmt.setInt(3, JobConstants.JOB_COMPLETED);
			pstmt.setInt(4, JobConstants.INITIAL_EXECUTION_COUNT);
			pstmt.setLong(5, orgId);
			pstmt.setLong(6, jobId);
			pstmt.setString(7, jobName);
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			LOGGER.error("Transaction error ",e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static long setInActiveAndUpdateCount(long orgId, long jobId, String jobName, int executionCount) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ?, CURRENT_EXECUTION_COUNT = ?, STATUS = ?, JOB_SERVER_ID= ?, EXECUTION_ERROR_COUNT = ? where ORGID = ? AND JOBID = ? AND JOBNAME = ?");
			
			pstmt.setBoolean(1, JobConstants.DISABLED);
			pstmt.setInt(2, executionCount);
			pstmt.setInt(3, JobConstants.JOB_COMPLETED);
			pstmt.setLong(4, JobConstants.DEFAULT_SERVER_ID);
			pstmt.setInt(5, JobConstants.INITIAL_EXECUTION_COUNT);
			pstmt.setLong(6, orgId);
			pstmt.setLong(7, jobId);
			pstmt.setString(8, jobName);
            return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			LOGGER.error("Error for job id "+jobId+ " : Jobname : "+jobName);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static long setInActive(long orgId, long jobId, String jobName) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ?, STATUS = ?, JOB_SERVER_ID= ?, EXECUTION_ERROR_COUNT = ? where ORGID = ? AND JOBID = ? AND JOBNAME = ?");
			
			pstmt.setBoolean(1, JobConstants.DISABLED);
			pstmt.setInt(2, JobConstants.JOB_COMPLETED);
			pstmt.setLong(3, JobConstants.DEFAULT_SERVER_ID);
			pstmt.setInt(4, JobConstants.INITIAL_EXECUTION_COUNT);
			pstmt.setLong(5, orgId);
			pstmt.setLong(6, jobId);
			pstmt.setString(7, jobName);
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}

//	public static List<JobContext> getJobs(String executorName, long startTime, long endTime, int maxRetry) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
//		return getJobs(executorName, startTime, endTime, maxRetry, null, null);
//	}

	private static void appendOrgId (StringBuilder sql, List<Long> orgIds, boolean isNot) {
		if (CollectionUtils.isNotEmpty(orgIds)) {
			sql.append(" AND ORGID");
			if (isNot) {
				sql.append(" NOT");
			}
			sql.append(" IN (")
					.append(StringUtils.join(orgIds, ","))
					.append(")");
		}
	}
	
	public static List<JobContext> getJobs(String executorName, long startTime, long endTime, int maxRetry, List<Long> include, List<Long> exclude, int limit) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		Connection conn = null;
		PreparedStatement getPstmt = null;
		ResultSet rs = null;
		
		List<JobContext> jcs = new ArrayList<JobContext>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();

			StringBuilder sql = new StringBuilder("SELECT * FROM Jobs WHERE EXECUTOR_NAME = ? AND IS_ACTIVE = ? AND STATUS = ? AND NEXT_EXECUTION_TIME < ? AND EXECUTION_ERROR_COUNT < ? ");
			appendOrgId(sql, include, false);
			appendOrgId(sql, exclude, true);
			sql.append(" ORDER BY NEXT_EXECUTION_TIME LIMIT ?");
			getPstmt = conn.prepareStatement(sql.toString());
			getPstmt.setString(1, executorName);
			getPstmt.setBoolean(2, JobConstants.ENABLED);
			getPstmt.setInt(3, JobConstants.JOB_COMPLETED);
			getPstmt.setLong(4, endTime);
			getPstmt.setInt(5, maxRetry);
			getPstmt.setInt(6,limit);
			rs = getPstmt.executeQuery();
			while(rs.next()) {
				jcs.add(getJobFromRS(rs));
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, getPstmt, rs);
		}
		
		return jcs;
	}

//	public static List<JobContext> getIncompletedJobs(String executorName, long startTime, long endTime, int maxRetry) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
//		return getIncompletedJobs(executorName, startTime, endTime, maxRetry, null, null);
//	}

	public static List<JobContext> getIncompletedJobs(String executorName, long startTime, long endTime, int maxRetry, List<Long> include, List<Long> exclude, int limit) throws SQLException, IOException, ParseException {
		Connection conn = null;
		PreparedStatement getPstmt = null;
		ResultSet rs = null;

		List<JobContext> jcs = new ArrayList<JobContext>();

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			StringBuilder sql = new StringBuilder("SELECT * FROM Jobs WHERE NEXT_EXECUTION_TIME < ? and EXECUTOR_NAME = ? AND IS_ACTIVE = ? AND STATUS = ? AND (CURRENT_EXECUTION_TIME + TRANSACTION_TIMEOUT) < ? AND EXECUTION_ERROR_COUNT < ? ");
			appendOrgId(sql, include, false);
			appendOrgId(sql, exclude, true);
			sql.append(" ORDER BY NEXT_EXECUTION_TIME LIMIT ?");
			getPstmt = conn.prepareStatement(sql.toString());
			getPstmt.setLong(1, endTime);
			getPstmt.setString(2, executorName);
			getPstmt.setBoolean(3, JobConstants.ENABLED);
			getPstmt.setInt(4, JobConstants.JOB_IN_PROGRESS);
			getPstmt.setLong(5, System.currentTimeMillis());
			getPstmt.setInt(6, maxRetry);
			getPstmt.setInt(7,limit);
			rs = getPstmt.executeQuery();
			while(rs.next()) {
				jcs.add(getJobFromRS(rs));
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, getPstmt, rs);
		}

		return jcs;
	}
	
	private static JobContext getJobFromRS(ResultSet rs) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		JobContext jc = new JobContext();
		
		jc.setJobId(rs.getLong("JOBID"));
		
		if(rs.getObject("ORGID") != null) {
			jc.setOrgId(rs.getLong("ORGID"));
		}
		
		jc.setJobName(rs.getString("JOBNAME"));

		if (rs.getObject("TIMEZONE") != null) {
			jc.setTimezone(rs.getString("TIMEZONE"));
		}
		
		jc.setActive(rs.getBoolean("IS_ACTIVE"));
		
		if(rs.getObject("TRANSACTION_TIMEOUT") != null) {
			jc.setTransactionTimeout(rs.getInt("TRANSACTION_TIMEOUT"));
		}
		
		jc.setIsPeriodic(rs.getBoolean("IS_PERIODIC"));
		
		if(rs.getObject("PERIOD") != null) {
			jc.setPeriod(rs.getInt("PERIOD"));
		}
		
		if(rs.getObject("SCHEDULE_INFO") != null) {
			jc.setScheduleJson(rs.getString("SCHEDULE_INFO"));
		}
		
		jc.setExecutionTime(rs.getLong("NEXT_EXECUTION_TIME"));
		
		jc.setExecutorName(rs.getString("EXECUTOR_NAME"));
		
		if(rs.getObject("END_EXECUTION_TIME") != null) {
			jc.setEndExecutionTime(rs.getLong("END_EXECUTION_TIME"));
		}
		
		if(rs.getObject("MAX_EXECUTION") != null) {
			jc.setMaxExecution(rs.getInt("MAX_EXECUTION"));
		}
		
		if(rs.getObject("CURRENT_EXECUTION_COUNT") != null) {
			jc.setCurrentExecutionCount(rs.getInt("CURRENT_EXECUTION_COUNT"));
		}

		if(rs.getObject("STATUS") != null) {
			jc.setStatus(rs.getInt("STATUS"));
		}

		if(rs.getObject("JOB_SERVER_ID") != null) {
			jc.setJobServerId(rs.getLong("JOB_SERVER_ID"));
		}

		if(rs.getObject("CURRENT_EXECUTION_TIME") != null) {
			jc.setJobStartTime(rs.getLong("CURRENT_EXECUTION_TIME"));
		}

		if(rs.getObject("EXECUTION_ERROR_COUNT") != null) {
			jc.setJobExecutionCount(rs.getInt("EXECUTION_ERROR_COUNT"));
		}

		if (rs.getObject("LOGGER_LEVEL") != null) {
			jc.setLoggerLevel(rs.getInt("LOGGER_LEVEL"));
		}
		
		return jc;
	}
	

	
	public static void deleteJob(long orgId, long jobId, String jobName) throws SQLException {
		String deleteSql = "DELETE FROM Jobs WHERE ORGID = ? AND JOBID = ? AND JOBNAME = ?";
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, jobId);
			pstmt.setString(3, jobName);
			if(pstmt.executeUpdate() < 1) {
				System.out.println("Deletion failed with Job ID : "+jobId+" and Jobname : "+jobName);
			}
			else {
				System.out.println("Successfully deleted job with Job ID : "+jobId+" and Jobname : "+jobName);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static int deleteJobs(long orgId, List<Long> jobIds, String jobName) throws Exception {
		FacilioModule module = ModuleFactory.getJobsModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getJobFields());
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("orgId"), String.valueOf(orgId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobId"), jobIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobName"), jobName, StringOperators.IS));
		
		return deleteBuilder.delete();
	}
	
	public static List<JobContext> getJobs(long orgId, List<Long> jobIds, String jobName) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		return getJobs(orgId, jobIds, jobName, null);
	}
	
	public static List<JobContext> getJobs(long orgId, List<Long> jobIds, String jobName, Boolean fetchActive) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = getPStmt(conn, orgId, jobIds, jobName, fetchActive); ResultSet rs = pstmt.executeQuery()) {
			List<JobContext> jcs = new ArrayList<>();
			while (rs.next()) {
				 jcs.add(getJobFromRS(rs));
			}
			return jcs;
		}
		catch (SQLException e) {
			LOGGER.error("Error",e);
			throw e;
		}
	}
	
	private static PreparedStatement getPStmt(Connection conn, long orgId, List<Long> ids, String jobName, Boolean fetchActive) throws SQLException {
		String q = createQuery(ids.size(), fetchActive);
		PreparedStatement pstmt = conn.prepareStatement(q);
		pstmt.setLong(1, orgId);
		pstmt.setString(2, jobName);
		int i = 3;
		for (Long id: ids) {
			pstmt.setLong(i, id);
			i++;
		}
		if (fetchActive != null) {
			pstmt.setBoolean(i, fetchActive);
		}
		return pstmt;
	}
	
	static int updateStartExecution(long orgId, long jobId, String jobName, long jobStartTime, int jobExecutionCount) {
		int rowsUpdated = 0;
		String query = "update Jobs set STATUS = 2, JOB_SERVER_ID = ?, CURRENT_EXECUTION_TIME = ?, EXECUTION_ERROR_COUNT = ? where ORGID = ? AND JOBID = ? and JOBNAME= ? and CURRENT_EXECUTION_TIME = ? and EXECUTION_ERROR_COUNT = ?";
		try(Connection connection = FacilioConnectionPool.getInstance().getDirectConnection();
			PreparedStatement statement = connection.prepareStatement(query)){
			statement.setLong(1, ServerInfo.getServerId());
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, jobExecutionCount+1);
			statement.setLong(4, orgId);
			statement.setLong(5, jobId);
			statement.setString(6, jobName);
			statement.setLong(7, jobStartTime);
			statement.setInt(8, jobExecutionCount);
			rowsUpdated = statement.executeUpdate();
			LOGGER.debug("query : " + statement.toString());
		} catch (SQLException e) {
			LOGGER.error("Exception while updating Job " + jobName + "_" + jobId, e);
		}
		LOGGER.debug("Updated Job " + jobName + " " + rowsUpdated );
		return rowsUpdated;
	}

	public static JobContext getJob(long orgId, long jobId, String jobName) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = getPStmt(conn, orgId, jobId, jobName); ResultSet rs = pstmt.executeQuery()) {
			if(rs.next()) {
				return getJobFromRS(rs);
			}
			return null;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error",e);

			throw e;
		}
	}

	private static PreparedStatement getPStmt(Connection conn, long orgId, long jobId, String jobName) throws SQLException {
		String sql = "SELECT * FROM Jobs WHERE ORGID = ? AND JOBID = ? AND JOBNAME = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1, orgId);
		pstmt.setLong(2, jobId);
		pstmt.setString(3, jobName);
		
		return pstmt;
	}
	
	private static String createQuery(int length, Boolean fetchActive) {
		String query = "SELECT * FROM Jobs WHERE ORGID = ? AND JOBNAME = ? AND JOBID in (";
		StringBuilder queryBuilder = new StringBuilder(query);
		for( int i = 0; i< length; i++){
			queryBuilder.append(" ?");
			if(i != length -1) queryBuilder.append(",");
		}
		queryBuilder.append(")");
		if (fetchActive != null) {
			queryBuilder.append(" AND IS_ACTIVE = ?");
		}
		return queryBuilder.toString();
	}
	
	public static int setStatusForJob(long orgId, long jobId, String jobName, boolean status) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ? where ORGID = ? AND JOBID = ? AND JOBNAME = ?");
			
			pstmt.setBoolean(1, status);
			pstmt.setLong(2, orgId);
			pstmt.setLong(3, jobId);
			pstmt.setString(4, jobName);
            return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			LOGGER.error("Error for job id "+jobId+ " : Jobname : "+jobName);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}

	public static List<JobContext> updateScheduledStatus ( List<JobContext> jobs ) {
		List<JobContext> scheduledJobs = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(jobs)) {
			for (JobContext job : jobs){
				int rowsUpdated = 0;
				String query = "update Jobs set STATUS = 4 where ORGID = ? AND JOBID = ? and JOBNAME= ? and EXECUTION_ERROR_COUNT = ? and STATUS != 4";
				try(Connection connection = FacilioConnectionPool.getInstance().getDirectConnection();
					PreparedStatement statement = connection.prepareStatement(query)){
					statement.setLong(1, job.getOrgId());
					statement.setLong(2, job.getJobId());
					statement.setString(3,job.getJobName());
					statement.setInt(4, job.getJobExecutionCount());
					rowsUpdated = statement.executeUpdate();
					if(rowsUpdated == 1){
						scheduledJobs.add(job);
					}
					LOGGER.debug("query : " + statement.toString());
				} catch (SQLException e) {
					LOGGER.error("Exception while updating Job " + job.getJobName() + "_" + job.getJobId(), e);
				}
				LOGGER.debug("Updated Job " + job.getJobName() + " " + rowsUpdated );
			}
		}
		LOGGER.debug("Successfully scheduled updated Jobs "+scheduledJobs.size() +" out of "+ jobs.size());
		return scheduledJobs;
	}
}
