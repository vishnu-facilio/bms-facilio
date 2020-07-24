package com.facilio.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.transaction.FTransactionManager;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class ServerInfo extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(ServerInfo.class.getName());
    private static long serverId = -1L;
    private static Connection connection;
    private static boolean localServerLeader = false;
    private static String hostname;

    private static final long PING_TIME_INTERVAL = 65_000L;

    private static final String SELECT_ID = "select id from server_info where private_ip = ?";
    private static final String INSERT_IP = "insert into server_info (private_ip, environment, status, pingtime, in_use, leader) values (?,?,?,?,?,?)";
    private static final String UPDATE_TIME = "update server_info set status = 1, pingtime = ? where id = ?";
    private static final String UPDATE_LEADER = "update server_info set leader = ? where id = ? and leader = ?";
    private static final String UPDATE_STATUS = "update server_info set status = ? where id = ? and status = ?";
    private static final String GET_SERVERS = "select id, pingtime from server_info where status = 1 order by id asc";
    private static final String GET_LEADER = "select id, pingtime from server_info where leader = 1";

    private static final String PING_TIME = "pingtime";
    private static final String ID = "id";
    private static final String SERVER_INFO_ID = "server_info_id";
    private static final String PRIVATE_IP = "private_ip";
    private static final String ENVIRONMENT = "environment";
    private static final String PINGTIME ="pingtime";
    private static final String IN_USE ="in_use";
    private static final String LEADER = "leader";
    private static final String STATUS = "status";

    private static final String SEREVR_TABLE_NAME = "server_info";
    private static final FacilioModule SEREVER_INFO = getFacilioAuditServerInfoTable();
    private static final FacilioField SEREVR_INFO_ID_FIELD = FieldFactory.getIdField(SERVER_INFO_ID, "id", SEREVER_INFO);
    private static final List<FacilioField> SERVER_FIELDS = getFacilioAuditServerInfoFields();
    
    private static FacilioModule getFacilioAuditServerInfoTable() {
        FacilioModule module = new FacilioModule();
        module.setTableName(SEREVR_TABLE_NAME);
        module.setDisplayName(SEREVR_TABLE_NAME);
        module.setName(SEREVR_TABLE_NAME);
        return module;
    }

    private static List<FacilioField> getFacilioAuditServerInfoFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(SEREVR_INFO_ID_FIELD);
        fields.add(FieldFactory.getStringField(PRIVATE_IP, PRIVATE_IP, SEREVER_INFO));
        fields.add(FieldFactory.getStringField(ENVIRONMENT, ENVIRONMENT, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(STATUS,STATUS, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(PINGTIME, PINGTIME, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(IN_USE, IN_USE, SEREVER_INFO));
        fields.add(FieldFactory.getNumberField(LEADER, LEADER, SEREVER_INFO));

        return fields;
    }

    
    private static void assignConnection() {
        try {
            connection = FacilioConnectionPool.getInstance().getConnection();
        } catch (SQLException e) {
            LOGGER.info("Exception while assigning connection ", e);
        }
    }

    public static void registerServer() throws Exception {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostName();
            hostname = ip;
            serverId = getServerId(ip);
            if(serverId == -1L) {
            	Map<String,Object> prop = new HashMap<>();
            	prop.put(PRIVATE_IP, ip);
            	serverId = addServer(prop,SEREVR_TABLE_NAME,SERVER_FIELDS);
            	if(serverId == 0) {
            		serverId = -1;
            	}
            }
        } catch (Exception e) {
            hostname = "-1";
            LOGGER.error("Unable to set IP ", e);
        }
    }

    public static String getHostname() {
        return hostname;
    }

    private static long getServerId(String ip) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            connection = FacilioConnectionPool.getInstance().getConnection();
            statement = connection.prepareStatement(SELECT_ID);
            statement.setString(1, ip);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getLong(ID);
            }
        } catch (SQLException e) {
            LOGGER.info("Exception while getting server id ", e);
        } finally {
            DBUtil.closeAll(connection, statement);
        }
        return -1L;
    }
    
    private static long addServer(Map<String, Object> prop, String tableName, List<FacilioField> serverFields) throws Exception {
    	 GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(tableName).fields(serverFields);
     	return insertBuilder.insert(prop);
    }
    
//    private static long addServerInfo(String ip) {
//        try (PreparedStatement insertQuery = connection.prepareStatement(INSERT_IP)){
//            LOGGER.info("Server id is empty ");
//            String environment = "user";
//            if (FacilioProperties.isScheduleServer()) {
//               environment = "scheduler";
//            }
//
//            insertQuery.setString(1, ip);
//            insertQuery.setString(2, environment);
//            insertQuery.setBoolean(3, true);
//            insertQuery.setLong(4, System.currentTimeMillis());
//            insertQuery.setBoolean(5, true);
//            insertQuery.setBoolean(6, false);
//            insertQuery.executeUpdate();
//        } catch (SQLException e) {
//            LOGGER.info("Exception while adding server info ", e);
//        }
//        return getServerId(ip);
//    }

    private static void markDownOutdatedServers () {
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(GET_SERVERS)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long lastPingFromServer = resultSet.getLong(PING_TIME);
                lastPingFromServer = lastPingFromServer + (4 * PING_TIME_INTERVAL);
                if (lastPingFromServer < System.currentTimeMillis()) {
                    long id = resultSet.getLong(ID);
                    updateStatus(id, false);
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Exception while marking servers down ", e);
        } finally {
            DBUtil.closeAll(null, resultSet);
        }
    }

    private static void checkAndAssignLeader () {
        ResultSet resultSet = null;
        try (PreparedStatement selectQuery = connection.prepareStatement(GET_LEADER)) {
            resultSet = selectQuery.executeQuery();
            long leaderId = -1;
            while(resultSet.next()){
                leaderId = resultSet.getLong(ID);
                long lastPingFromLeader =  resultSet.getLong(PING_TIME);
                lastPingFromLeader = lastPingFromLeader + PING_TIME_INTERVAL;
                if(lastPingFromLeader < System.currentTimeMillis()) {
                    markLeaderDownAndChooseNewLeader(leaderId);
                } else {
                    if (leaderId == serverId) {
                        localServerLeader = true;
                    }
                }
            }
            if(localServerLeader) {
                markDownOutdatedServers();
            }
            if(leaderId == -1) {
                int updatedRows = updateLeader(serverId, true);
                if(updatedRows == 1) {
                    localServerLeader = true;
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Exception in checkAndAssignLeader ", e);
        } finally {
            DBUtil.closeAll(null, resultSet);
        }
    }

    private static void markLeaderDownAndChooseNewLeader(long leaderId) {
        FTransactionManager transaction = (FTransactionManager) FTransactionManager.getTransactionManager();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            transaction.begin();
            int updatedRows = updateLeader(leaderId, false);
            if(updatedRows == 1) {
                LOGGER.info("Marked old leader as subject id: " + leaderId);
                statement = connection.prepareStatement(GET_SERVERS);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    long newLeaderId = resultSet.getLong(ID);
                    long newLeaderLastPingTime = resultSet.getLong(PING_TIME);
                    newLeaderLastPingTime = newLeaderLastPingTime + PING_TIME_INTERVAL;
                    if (newLeaderLastPingTime > System.currentTimeMillis()) {
                        updatedRows = updateLeader(newLeaderId, true);
                        if (updatedRows == 1) {
                            return;
                        }
                    }
                }
            }

            transaction.commit();
        } catch (SQLException | NotSupportedException | HeuristicMixedException | SystemException | RollbackException | HeuristicRollbackException e) {
            LOGGER.info("Exception in markLeaderDownAndChooseNewLeader ", e);
            try {
                transaction.rollback();
            } catch (SystemException e1) {
                LOGGER.info("Exception while rolling back in markLeaderDownAndChooseNewLeader ", e1);
            }
        } finally {
                DBUtil.closeAll(statement, resultSet);
        }

    }

    private static int updateLeader(long id, boolean status) {
        if(id > 0) {
            try (PreparedStatement updateQuery = connection.prepareStatement(UPDATE_LEADER)) {
                updateQuery.setBoolean(1, status);
                updateQuery.setLong(2, id);
                updateQuery.setBoolean(3, !status);
                return updateQuery.executeUpdate();
            } catch (SQLException e) {
                LOGGER.info("Exception while updating leader for id : " + id , e);
            }
        }
        return 0;
    }

    private static int updateStatus(long id, boolean status) {
        if(id > 0) {
            try (PreparedStatement updateQuery = connection.prepareStatement(UPDATE_STATUS)) {
                updateQuery.setBoolean(1, status);
                updateQuery.setLong(2, id);
                updateQuery.setBoolean(3, !status);
                return updateQuery.executeUpdate();
            } catch (SQLException e) {
                LOGGER.info("Exception while updating server status for id : " + id, e);
            }
        }
        return 0;
    }

    public static long getServerId() {
        return serverId;
    }

    public void run() {
        if(serverId > 0) {
            try (PreparedStatement updateQuery = connection.prepareStatement(UPDATE_TIME)) {
                updateQuery.setLong(1, System.currentTimeMillis());
                updateQuery.setLong(2, serverId);
                updateQuery.executeUpdate();
                checkAndAssignLeader();
            } catch (SQLException e) {
                LOGGER.info("Exception while updating ping time ", e);
            }
        }
    }
}
