package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.sqlitecomparitor.SQLiteComparitor;
import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Map;

public class SQLiteAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(SQLiteAction.class.getName());
    @NotNull
    private Long agentId;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @NotNull
    private File sqLiteFile;

    public File getSqLiteFile() {
        return sqLiteFile;
    }

    public void setSqLiteFile(File sqLiteFile) {
        this.sqLiteFile = sqLiteFile;
    }

    public String validateDb() {
        try {
            LOGGER.info(getSqLiteFile().length());
            FacilioAgent agent = AgentApiV2.getAgent(getAgentId());
            SQLiteComparitor comparitor = new SQLiteComparitor(getSqLiteFile(), agent);
            comparitor.connect();
            for (FacilioControllerType type : FacilioControllerType.values()) {
                comparitor.sqliteMinusDb(type);
                comparitor.dbMinusSqlite(type);
            }
            Map<String, Controller> sqliteControllersMinusDbControllers = comparitor.getSqliteControllersMinusDbControllers();
            Map<String, List<String>> sqlitePointsMinusDbPoints = comparitor.getSqlitePointsMinusDbPoints();
            Map<String, Controller> dbControllersMinusSqliteControllers = comparitor.getDbControllersMinusSqliteControllers();
            Map<String, List<String>> dbPointsMinusSqlitePoints = comparitor.getDbPointsMinusSqlitePoints();
            Map<Controller, Controller> conflictingControllers = comparitor.getConflictingControllers();
            Map<String, Map<Point, Point>> conflictingPoints = comparitor.getConflictingPoints();
            comparitor.closeConnections();

            setResult("sqliteControllersMinusDbControllers", sqliteControllersMinusDbControllers.size());
            setResult("sqlitePointsMinusDbPoints", sqlitePointsMinusDbPoints.size());
            setResult("dbControllersMinusSqliteControllers", dbControllersMinusSqliteControllers.size());
            setResult("dbPointsMinusSqlitePoints", dbPointsMinusSqlitePoints.size());

            LOGGER.info("sc-dc :" + sqliteControllersMinusDbControllers.size());
            LOGGER.info("sp-dp :" + sqlitePointsMinusDbPoints.size());
            LOGGER.info("dc-sc :" + dbControllersMinusSqliteControllers.size());
            LOGGER.info("dp-sp :" + dbPointsMinusSqlitePoints.size());

            LOGGER.info("cc : " + conflictingControllers.size());

        } catch (Exception ex) {
            LOGGER.info("Exception while validating ", ex);
        }
        return SUCCESS;
    }
}
