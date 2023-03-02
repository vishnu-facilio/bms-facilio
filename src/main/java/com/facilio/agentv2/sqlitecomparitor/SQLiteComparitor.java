package com.facilio.agentv2.sqlitecomparitor;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
//import com.facilio.sqlUtils.constants.Columns;
//import com.facilio.sqlUtils.tables.BacNetIpControllerTable;
//import com.facilio.sqlUtils.tables.BacNetIpPointTable;
//import com.facilio.sqlUtils.tables.ControllerTable;
//import com.facilio.sqlUtils.tables.PointTable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.json.simple.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLiteComparitor {

   /* private static final Logger LOGGER = LogManager.getLogger(SQLiteComparitor.class.getName());

    private File sqLiteFile;
    private Connection connection = null;
    private Statement statement = null;
    private FacilioAgent agent;
    private Map<String, Controller> sqliteControllersMinusDbControllers = new HashMap<>();
    private Map<String, Controller> dbControllersMinusSqliteControllers = new HashMap<>();
    private Map<Controller, Controller> conflictingControllers = new HashMap<>();

    private Map<String, List<String>> sqlitePointsMinusDbPoints = new HashMap<>();
    private Map<String, List<String>> dbPointsMinusSqlitePoints = new HashMap<>();
    private Map<String, Map<Point, Point>> conflictingPoints = new HashMap<>();

    public Map<String, Controller> getDbControllersMinusSqliteControllers() {
        return dbControllersMinusSqliteControllers;
    }

    public Map<String, List<String>> getDbPointsMinusSqlitePoints() {
        return dbPointsMinusSqlitePoints;
    }

    public Map<String, List<String>> getSqlitePointsMinusDbPoints() {
        return sqlitePointsMinusDbPoints;
    }

    public Map<String, Map<Point, Point>> getConflictingPoints() {
        return conflictingPoints;
    }

    public Map<String, Controller> getSqliteControllersMinusDbControllers() {
        return sqliteControllersMinusDbControllers;
    }

    public Map<Controller, Controller> getConflictingControllers() {
        return conflictingControllers;
    }

    public SQLiteComparitor(File sqLiteFile, FacilioAgent agent) {
        this.sqLiteFile = sqLiteFile;
        this.agent = agent;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.sqLiteFile.getAbsolutePath());
            statement = connection.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void sqliteMinusDb(FacilioControllerType controllerType) {

        Map<String, Controller> sqliteControllersMinusDbControllers = new HashMap<>();
        try {
            long offset = 0;
            long limit = 100;
            Map<String, Controller> sqliteControllerMap = getControllersFromSqlite(limit, offset, controllerType);
            while (sqliteControllerMap.size() > 0) {
                Set<String> sqliteControllersName = new HashSet<>();
                sqliteControllerMap.values().forEach(controller -> sqliteControllersName.add(controller.getName()));
                List<Controller> controllersFromDb = AgentConstants.getControllerBean().getControllersByNames(agent.getId(), sqliteControllersName, FacilioControllerType.BACNET_IP);
                Map<String, Controller> controllersFromDbMap = new HashMap<>();
                controllersFromDb.forEach(controller -> controllersFromDbMap.put(controller.getName(), controller));
                if (controllersFromDbMap.values().size() < sqliteControllerMap.values().size()) {
                    LOGGER.info("Conflict occurred : Missing controllers in db");
                    Set<String> s1 = new HashSet<String>(sqliteControllerMap.keySet());
                    Set<String> s2 = new HashSet<String>(controllersFromDbMap.keySet());
                    s1.removeAll(s2);
                    for (String controllerName : s1) {
                        sqliteControllersMinusDbControllers.put(controllerName, sqliteControllerMap.get(controllerName));
                    }
                }
                for (Controller dbController : controllersFromDbMap.values()) {
                    Controller sqliteController = sqliteControllerMap.get(dbController.getName());
                    if (!isControllersEqual(dbController, sqliteController, controllerType)) {
                        conflictingControllers.put(dbController, sqliteController);
                    } else {
                        sqlitePointsMinusDbPoints(dbController, sqliteController, controllerType);
                    }
                }
                offset = offset + limit;
                sqliteControllerMap = getControllersFromSqlite(limit, offset, controllerType);
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while", ex);
        }
        this.sqliteControllersMinusDbControllers.putAll(sqliteControllersMinusDbControllers);
    }

    private void sqlitePointsMinusDbPoints(Controller dbController, Controller sqliteController, FacilioControllerType controllerType) {
        try {
            long offset = 0;
            long limit = 100;
            Set<Point> sqlitePoints = getPointsFromSqlite(limit, offset, sqliteController, controllerType);
            while (sqlitePoints.size() > 0) {
                List<String> sqlitePointsName = new ArrayList<>();
                sqlitePoints.forEach(p -> sqlitePointsName.add(p.getName()));
                List<Point> pointsFromDb = PointsAPI.getControllerPointsFromNames(controllerType, dbController, sqlitePointsName);
                if (pointsFromDb.size() < sqlitePoints.size()) {
                    LOGGER.info("some missing points in db");
                    Set<String> s1 = new HashSet<>();
                    sqlitePoints.forEach(p -> s1.add(p.getName()));
                    Set<String> s2 = new HashSet<>();
                    pointsFromDb.forEach(p -> s2.add(p.getName()));
                    s1.removeAll(s2);
                    if (this.sqlitePointsMinusDbPoints.get(sqliteController.getName()) == null) {
                        List<String> missingPointNameList = new ArrayList<>(s1);
                        s1.forEach(name -> this.sqlitePointsMinusDbPoints.put(sqliteController.getName(), missingPointNameList));
                    } else {
                        s1.forEach(name -> this.sqlitePointsMinusDbPoints.get(sqliteController.getName()).addAll(s1));
                    }
                }
                Map<String, Point> sqlitePointMap = new HashMap<>();
                sqlitePoints.forEach(p -> sqlitePointMap.put(p.getName(), p));
                for (Point point : pointsFromDb) {
                    if (!isPointsEqual(point, sqlitePointMap.get(point.getName()), controllerType)) {
                        if (conflictingPoints.get(sqliteController.getName()) != null) {
                            conflictingPoints.get(sqliteController.getName()).put(point, sqlitePointMap.get(point.getName()));
                        } else {
                            Map<Point, Point> map = new HashMap<>();
                            map.put(point, sqlitePointMap.get(point.getName()));
                            conflictingPoints.put(sqliteController.getName(), map);
                        }

                    }
                }
                offset = offset + limit;
                sqlitePoints = getPointsFromSqlite(limit, offset, sqliteController, controllerType);
            }

        } catch (Exception ex) {
            LOGGER.info("Error ", ex);
        }

    }


    public Map<String, Controller> dbMinusSqlite(FacilioControllerType controllerType) {

        try {
            int offset = 0;
            int limit = 100;
            Map<String, Controller> dbControllersMap = getControllersFromDb(controllerType);
            while (dbControllersMap.size() > 0) {
                Set<String> dbControllerNames = new HashSet<>();
                dbControllersMap.forEach((k, v) -> dbControllerNames.add(k));
                Map<String, Controller> sqliteControllersMap = getControllersFromSqlite(dbControllerNames, controllerType);

                if (sqliteControllersMap.values().size() < dbControllersMap.values().size()) {
                    LOGGER.info("Conflict occurred : Missing controllers in sqlite");
                    Set<String> s1 = new HashSet<String>(dbControllersMap.keySet());
                    Set<String> s2 = new HashSet<String>(sqliteControllersMap.keySet());
                    s1.removeAll(s2);
                    for (String controllerName : s1) {
                        dbControllersMinusSqliteControllers.put(controllerName, dbControllersMap.get(controllerName));
                    }
                }
                for (Controller sqliteController : sqliteControllersMap.values()) {
                    Controller dbController = dbControllersMap.get(sqliteController.getName());
                    if (!isControllersEqual(dbController, sqliteController, controllerType)) {
                        conflictingControllers.put(dbController, sqliteController);
                    } else {
                        dbPointsMinusSqlitePoints(dbController, sqliteController, controllerType);
                    }
                }

                offset = offset + limit;
                dbControllersMap = getControllersFromDb(controllerType);
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while", ex);
        }
        return dbControllersMinusSqliteControllers;
    }

    *//* List<Device> getDevicesFromDb(int limit, int offset, FacilioControllerType controllerType) {
         List<Device> devices = new ArrayList<>();
         try {
             FacilioContext context = new FacilioContext();
             context.put(AgentConstants.AGENT_ID, agent.getId());
             context.put(AgentConstants.CONTROLLER_TYPE, controllerType.asInt());
             context.put(FacilioConstants.ContextNames.PAGINATION, getPagination(limit, offset));
             context.put(FacilioConstants.ContextNames.CONFIGURE, "1");
             devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevices(context), Device.class);
         } catch (Exception e) {
             LOGGER.info("Exception while fetching devices from db", e);
         }
         return devices;
     }
     *//*
    private void dbPointsMinusSqlitePoints(Controller dbController, Controller sqliteController, FacilioControllerType controllerType) {
        try {
            int offset = 0;
            int limit = 100;
            Set<Point> dbPoints = getPointsFromDb(limit, offset, dbController, controllerType);
            while (dbPoints.size() > 0) {
                List<String> dbPointsName = new ArrayList<>();
                dbPoints.forEach(p -> dbPointsName.add(p.getName()));
                List<Point> sqlitePoints = getPointsFromSqlite(sqliteController, controllerType, dbPointsName);

                if (sqlitePoints.size() < dbPoints.size()) {
                    LOGGER.info("some missing points in sqlite");
                    Set<String> s1 = new HashSet<>();
                    dbPoints.forEach(p -> s1.add(p.getName()));
                    Set<String> s2 = new HashSet<>();
                    sqlitePoints.forEach(p -> s2.add(p.getName()));
                    s1.removeAll(s2);
                    if (this.dbPointsMinusSqlitePoints.get(dbController.getName()) == null) {
                        List<String> missingPointNameList = new ArrayList<>(s1);
                        s1.forEach(name -> this.dbPointsMinusSqlitePoints.put(dbController.getName(), missingPointNameList));
                    } else {
                        s1.forEach(name -> this.dbPointsMinusSqlitePoints.get(dbController.getName()).addAll(s1));
                    }
                }
                Map<String, Point> dbPointsMap = new HashMap<>();
                dbPoints.forEach(p -> dbPointsMap.put(p.getName(), p));
                for (Point point : sqlitePoints) {
                    if (!isPointsEqual(point, dbPointsMap.get(point.getName()), controllerType)) {
                        if (conflictingPoints.get(dbController.getName()) != null) {
                            conflictingPoints.get(dbController.getName()).put(point, dbPointsMap.get(point.getName()));
                        } else {
                            Map<Point, Point> map = new HashMap<>();
                            map.put(point, dbPointsMap.get(point.getName()));
                            conflictingPoints.put(dbController.getName(), map);
                        }

                    }
                }
                offset = offset + limit;
                dbPoints = getPointsFromDb(limit, offset, dbController, controllerType);
            }

        } catch (Exception ex) {
            LOGGER.info("Error ", ex);
        }
    }

    private Set<Point> getPointsFromDb(int limit, int offset, Controller dbController, FacilioControllerType controllerType) {
        List<Point> pointsData = new ArrayList<>();
        GetPointRequest getPointRequest = null;
        FacilioContext paginationContext = new FacilioContext();
        paginationContext.put(FacilioConstants.ContextNames.PAGINATION, getPagination(limit, offset));
        try {
            getPointRequest = new GetPointRequest()
                    .withControllerId(dbController.getControllerId())
                    .ofType(controllerType)
                    .pagination(paginationContext);
            pointsData = getPointRequest.getPoints();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while fetching paged points from DB", e);
        }
        return new HashSet<>(pointsData);
    }

    private List<Point> getPointsFromSqlite(Controller sqliteController, FacilioControllerType controllerType, List<String> dbPointsName) {
        Set<Point> points = new HashSet<>();
        ResultSet resultSet = null;
        try {
            switch (controllerType) {
                case BACNET_IP:
                    resultSet = statement.executeQuery("SELECT * FROM " + PointTable.getTable().getName() + " inner join " + BacNetIpPointTable.getTable().getName() +
                            " on " + PointTable.getTable().getName() + ".id" + "=" + BacNetIpPointTable.getTable().getName() + ".id where " +
                            PointTable.getTable().getName() + ".controllerId = " + sqliteController.getId() + " and " + PointTable.getTable().getName() + ".name in (" +
                            StringUtil.join(",", dbPointsName) + ")");
                    while (resultSet.next()) {
                        BacnetIpPointContext bacnetIpPointContext = new BacnetIpPointContext();
                        bacnetIpPointContext.setName(resultSet.getString(Columns.NAME));
                        bacnetIpPointContext.setInstanceNumber(resultSet.getInt(Columns.INSTANCE_NUMBER));
                        bacnetIpPointContext.setInstanceType(resultSet.getInt(Columns.INSTANCE_TYPE));
                        updatePoint(resultSet, bacnetIpPointContext);
                        points.add(bacnetIpPointContext);
                    }
                    break;
                case OPC_XML_DA:
                    //TODO
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while getting points from sqlite", ex);
        }
        return new ArrayList<>(points);
    }

    private Map<String, Controller> getControllersFromSqlite(Set<String> dbControllerNames, FacilioControllerType controllerType) {
        Map<String, Controller> sqliteNameVsController = new HashMap<>();
        ResultSet sqliteControllersResultSet = null;
        switch (controllerType) {
            case BACNET_IP:
                try {
                    String query = "SELECT * FROM " + ControllerTable.getTable().getName() + " inner join " + BacNetIpControllerTable.getTable().getName() +
                            " on " + ControllerTable.getTable().getName() + ".id" + "=" + BacNetIpControllerTable.getTable().getName() + ".id " +
                            " where " + ControllerTable.getTable().getName() + ".name in (" +
                            StringUtil.join("','", dbControllerNames).replace('[', '\'').replace(']', '\'')
                            + ")";
                    LOGGER.info("SQLite query : " + query);
                    sqliteControllersResultSet = statement.executeQuery(query);
                    while (sqliteControllersResultSet.next()) {
                        BacnetIpControllerContext bacnetIpControllerContext = new BacnetIpControllerContext();
                        bacnetIpControllerContext.setName(sqliteControllersResultSet.getString(Columns.NAME));
                        bacnetIpControllerContext.setInstanceNumber(sqliteControllersResultSet.getInt(Columns.INSTANCE_NUMBER));
                        bacnetIpControllerContext.setNetworkNumber(sqliteControllersResultSet.getInt(Columns.NETWORK_NUMBER));
                        bacnetIpControllerContext.setIpAddress(sqliteControllersResultSet.getString(Columns.IP_ADDRESS));
                        bacnetIpControllerContext.setId(sqliteControllersResultSet.getInt(Columns.ID));
                        sqliteNameVsController.put(bacnetIpControllerContext.getName(), bacnetIpControllerContext);
                    }
                    sqliteControllersResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case OPC_XML_DA:
                //TODO
        }
        return sqliteNameVsController;
    }

    private Map<String, Controller> getControllersFromDb(FacilioControllerType controllerType) {
        Map<String, Controller> controllerMap = new HashMap<>();
        try {
            List<Controller> controllers = new GetControllerRequest().withAgentId(agent.getId()).ofType(controllerType).getControllers();
                controllers.forEach(c -> controllerMap.put(c.getName(), c));

        } catch (Exception e) {
            LOGGER.info("Exception while getting controllers from db ", e);
        }
        return controllerMap;
    }

    private Map<String, Controller> getControllersFromSqlite(long limit, long offset, FacilioControllerType controllerType) {
        Map<String, Controller> sqliteNameVsController = new HashMap<>();
        ResultSet sqliteControllersResultSet = null;
        switch (controllerType) {
            case BACNET_IP:
                try {
                    sqliteControllersResultSet = statement.executeQuery("SELECT * FROM " + ControllerTable.getTable().getName() + " inner join " + BacNetIpControllerTable.getTable().getName() +
                            " on " + ControllerTable.getTable().getName() + ".id" + "=" + BacNetIpControllerTable.getTable().getName() + ".id limit " + limit + " offset " + offset);
                    while (sqliteControllersResultSet.next()) {
                        BacnetIpControllerContext bacnetIpControllerContext = new BacnetIpControllerContext();
                        bacnetIpControllerContext.setName(sqliteControllersResultSet.getString(Columns.NAME));
                        bacnetIpControllerContext.setInstanceNumber(sqliteControllersResultSet.getInt(Columns.INSTANCE_NUMBER));
                        bacnetIpControllerContext.setNetworkNumber(sqliteControllersResultSet.getInt(Columns.NETWORK_NUMBER));
                        bacnetIpControllerContext.setIpAddress(sqliteControllersResultSet.getString(Columns.IP_ADDRESS));
                        bacnetIpControllerContext.setId(sqliteControllersResultSet.getInt(Columns.ID));
                        sqliteNameVsController.put(bacnetIpControllerContext.getName(), bacnetIpControllerContext);
                    }
                    sqliteControllersResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case OPC_XML_DA:
                //TODO
        }
        return sqliteNameVsController;
    }

    private Set<Point> getPointsFromSqlite(long limit, long offset, Controller sqliteController, FacilioControllerType controllerType) {
        Set<Point> points = new HashSet<>();
        ResultSet resultSet = null;
        try {
            switch (controllerType) {
                case BACNET_IP:
                    resultSet = statement.executeQuery("SELECT * FROM " + PointTable.getTable().getName() + " inner join " + BacNetIpPointTable.getTable().getName() +
                            " on " + PointTable.getTable().getName() + ".id" + "=" + BacNetIpPointTable.getTable().getName() + ".id where " + PointTable.getTable().getName() + ".controllerId = " + sqliteController.getId() + " limit " + limit + " offset " + offset);
                    while (resultSet.next()) {
                        BacnetIpPointContext bacnetIpPointContext = new BacnetIpPointContext();
                        bacnetIpPointContext.setName(resultSet.getString(Columns.NAME));
                        bacnetIpPointContext.setInstanceNumber(resultSet.getInt(Columns.INSTANCE_NUMBER));
                        bacnetIpPointContext.setInstanceType(resultSet.getInt(Columns.INSTANCE_TYPE));
                        updatePoint(resultSet, bacnetIpPointContext);
                        points.add(bacnetIpPointContext);
                    }
                    break;
                case OPC_XML_DA:
                    //TODO
                default:
                    throw new Exception("Unhandled controller type");
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while getting points from sqlite", ex);
        }
        points.forEach(p -> {
            try {
                p.getConfigureStatus();
            } catch (Exception ex) {
                LOGGER.info("Configure Status conflict ", ex);
            }
        });
        return points;
    }

    private void updatePoint(ResultSet resultSet, Point point) throws Exception {
        if (resultSet.getInt(Columns.IN_USE) == 1) {
            point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
        } else {
            point.setConfigureStatus(PointEnum.ConfigureStatus.UNCONFIGURED.getIndex());
        }
        if (resultSet.getInt(Columns.SUBSCRIBED) == 1) {
            point.setSubscribeStatus(3);
        } else {
            point.setSubscribeStatus(0);
        }
        if (resultSet.getInt(Columns.WRITABLE) == 1) {
            point.setWritable(true);
        } else {
            point.setWritable(false);
        }
    }

    public void closeConnections() throws Exception {
        statement.close();
        connection.close();
    }

    private boolean isControllersEqual(Controller controller, Controller controller1, FacilioControllerType controllerType) {
        switch (controllerType) {
            case BACNET_IP:
                BacnetIpControllerContext b1 = (BacnetIpControllerContext) controller;
                BacnetIpControllerContext b2 = (BacnetIpControllerContext) controller1;
                return (b1.getInstanceNumber() == b2.getInstanceNumber() &&
                        b1.getNetworkNumber() == b2.getNetworkNumber() &&
                        b1.getIpAddress().equals(b2.getIpAddress())
                );
            case OPC_XML_DA:
                //TODO
            default:
                return false;
        }
    }

    private boolean isPointsEqual(Point point, Point point1, FacilioControllerType controllerType) {

        try {
            if (point.getConfigureStatus() == point1.getConfigureStatus()
                    && point.isSubscribed() == point1.isSubscribed()
                    && point.isWritable() == point1.isWritable()) {
                switch (controllerType) {
                    case BACNET_IP:
                        BacnetIpPointContext b1 = (BacnetIpPointContext) point;
                        BacnetIpPointContext b2 = (BacnetIpPointContext) point1;
                        return (b1.getInstanceNumber() == b2.getInstanceNumber() &&
                                b1.getInstanceType() == b2.getInstanceType());
                    case OPC_XML_DA:
                        //TODO
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while comparing DB points: " + point.toJSON() + "\n"
                    + "And SQLITE Point : " + point1.toJSON(), ex);
            return false;
        }
    }

    private static JSONObject getPagination(int limit, int offset) {
        JSONObject paginationObject = new JSONObject();
        paginationObject.put("page", offset == 0 ? 1 : (offset / limit) + 1);
        paginationObject.put("perPage", limit);
        return paginationObject;
    }*/
}
