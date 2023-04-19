package com.facilio.agentv2.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.chain.FacilioContext;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ControllerActions extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(ControllerActions.class.getName());

    @NotNull
    @Min(value = 0, message = "Controller Id can't be less than 1")
    private Long controllerId;
    @Getter
    @Setter
    private InputStream downloadStream;
    @Getter
    @Setter
    String fileName;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public String getControllerUsingId() {
        try {
            List<Map<String, Object>> controller = AgentConstants.getControllerBean().getControllerData(null, getControllerId(), constructListContext(new FacilioContext()));
            if (controller != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                ok();
                setResult(AgentConstants.DATA, controller);
                return SUCCESS;
            } else {
                noContent();
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller using orgId", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        setResult(AgentConstants.DATA, new JSONObject());
        return SUCCESS;
    }


    public String discoverPoints() {
        try {
            if (ControllerUtilV2.discoverPoints(getControllerId())) {
                setResult(AgentConstants.RESULT, SUCCESS);
            }
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception while discoverPoints", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String resetController() {
        try {
            AgentConstants.getControllerBean().resetController(getControllerId());
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while reset controller");
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    }

    public String getConfiguredPoints() throws Exception {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterConfigurePoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getSubscribedPoints() throws Exception {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterSubscribedPoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getUnconfiguredPoints() throws Exception {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterUnConfigurePoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getCommissionedPoints() throws Exception {
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterCommissionedPoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA, points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String exportPoints() {

        try {
            GetPointRequest getPointRequest = new GetPointRequest().withControllerId(controllerId);
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            if (points.size() > 0) {
                String dirPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "points";
                String path = dirPath + File.separator + controllerId + ".csv";
                setFileName(controllerId + ".csv");
                File file = new File(path);
                File dir = new File(dirPath);
                boolean directoryExits = (dir.exists() && dir.isDirectory());
                if (!directoryExits) {
                    dir.mkdirs();
                }
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                StringBuilder content = new StringBuilder();
                points.get(0).keySet().forEach(key -> {
                    content.append(key);
                    content.append(",");
                });
                content.append("\n");
                points.forEach(row -> {
                    row.forEach((colName, value) -> {
                        content.append(value);
                        content.append(",");
                    });
                    content.append("\n");
                });
                fos.write(content.toString().getBytes());
                fos.close();
                downloadStream = new FileInputStream(path);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return SUCCESS;
    }
}
