package com.facilio.server;

import com.facilio.aws.util.AwsUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.TimerTask;

public class ServerInfo extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(ServerInfo.class.getName());
    private static long serverId = -1L;
    private static Connection connection;
    private static boolean localServerLeader = false;

    private static final long PING_TIME_INTERVAL = 65_000L;

    private static final String SELECT_ID = "select id from server_info where private_ip = ?";
    private static final String INSERT_IP = "insert into server_info (private_ip, environment, status, pingtime, in_use, leader) values (?,?,?,?,?,?)";
    private static final String UPDATE_TIME = "update server_info set status = 1, pingtime = ? where id = ?";
    private static final String UPDATE_LEADER = "update server_info set leader = ? where id = ?";
    private static final String GET_SERVERS = "select id, pingtime from server_info where status = 1 order by asc desc";
    private static final String GET_LEADER = "select id, pingtime from server_info where leader = 1";

    static {
        assignConnection();
    }

    private static void assignConnection() {
        try {
            connection = FacilioConnectionPool.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerServer() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            serverId = getServerId(ip);
            if(serverId == -1L) {
                serverId = addServerInfo(ip);
            }
        } catch (UnknownHostException e) {
            LOGGER.info("Unable to set IP ");
        }
    }

    private static long getServerId(String ip) {
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ID)){
            statement.setString(1, ip);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(null, resultSet);
        }
        return -1L;
    }

    private static long addServerInfo(String ip) {
        try (PreparedStatement insertQuery = connection.prepareStatement(INSERT_IP)){
            LOGGER.info("Server id is empty ");
            String environment = AwsUtil.getConfig("environment");
            if(environment != null) {
                environment = environment.trim().toLowerCase();
            }
            insertQuery.setString(1, ip);
            insertQuery.setString(2, environment);
            insertQuery.setBoolean(3, true);
            insertQuery.setLong(4, System.currentTimeMillis());
            insertQuery.setBoolean(5, true);
            insertQuery.setBoolean(6, false);
            insertQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getServerId(ip);
    }

    private static void markDownOutdatedServers () {

    }

    private static void checkAndAssignLeader () {
        ResultSet resultSet = null;
        try (PreparedStatement selectQuery = connection.prepareStatement(GET_LEADER)) {
            resultSet = selectQuery.executeQuery();
            long leaderId = -1;
            while(resultSet.next()){
                leaderId = resultSet.getLong("id");
                long lastPingFromLeader =  resultSet.getLong("pingtime");
                lastPingFromLeader = lastPingFromLeader + PING_TIME_INTERVAL;
                if(lastPingFromLeader < System.currentTimeMillis()) {
                    markLeaderDownAndChooseNewLeader(leaderId);
                } else {
                    if (leaderId == serverId) {
                        localServerLeader = true;
                    }
                }
            }

            if(leaderId == -1) {
                updateLeader(serverId, true);
            }
        } catch (SQLException e) {

        }
    }

    private static void markLeaderDownAndChooseNewLeader(long leaderId) {
        updateLeader(leaderId, false);
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement(GET_SERVERS)) {
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                long newLeaderId = resultSet.getLong("id");
                long newLeaderLastPingTime = resultSet.getLong("pingtime");
                newLeaderLastPingTime = newLeaderLastPingTime + PING_TIME_INTERVAL;
                if(newLeaderLastPingTime > System.currentTimeMillis()) {
                    updateLeader(newLeaderId, true);
                    return;
                }
            }
            Thread.sleep(30000L);
        } catch (SQLException | InterruptedException e) {

        }

    }

    private static void updateLeader(long id, boolean status) {
        if(id > 0) {
            try (PreparedStatement updateQuery = connection.prepareStatement(UPDATE_LEADER)) {
                updateQuery.setBoolean(1, status);
                updateQuery.setLong(2, id);
                updateQuery.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        if(serverId > 0) {
            try (PreparedStatement updateQuery = connection.prepareStatement(UPDATE_TIME)) {
                updateQuery.setLong(1, System.currentTimeMillis());
                updateQuery.setLong(2, serverId);
                updateQuery.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
