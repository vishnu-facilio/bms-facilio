package com.facilio.agentv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FacilioFileStore;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.modbusrtu.ModbusImportUtils;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.agentv2.upgrade.AgentVersionApi;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.actions.AdminAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.messageQueue.MessageQueueTopic;


public class AgentAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentAction.class.getName());
    private String instanceType;
    
    /* public String createPolicy(){
            CreateKeysAndCertificateResult cert = AwsUtil.signUpIotToKinesis(AccountUtil.getCurrentOrg().getDomain(), AccountUtil.getCurrentOrg().getDomain(), "facilio");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cert arn",cert.getCertificateArn());
            jsonObject.put("cert id",cert.getCertificateId());
            jsonObject.put("cert cert pem",cert.getCertificatePem());
            setResult("Data",jsonObject);

        return SUCCESS;
    }*/

   

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String listAgents() {
        try {
            FacilioChain chain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
            FacilioContext context = chain.getContext();
            constructListContext(context);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.AGENT);
            chain.execute();
            Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
            AgentBean agentBean = getAgentBean();
            List<Map<String, Object>> agentListData = agentBean.getAgentListData(false,querySearch,getPagination(),defaultIds,filterCriteria);
            // agentBean.listFacilioAgents(context);
            int offLineAgents = 0;
            Set<Long> siteCount = new HashSet<>();
            for (Map<String, Object> agentListDatum : agentListData) {
               offLineAgents += AgentUtilV2.getAgentOfflineStatus(agentListDatum);
            }
            setResult(AgentConstants.SITE_COUNT, siteCount.size());
            setResult(AgentConstants.TOTAL_COUNT, agentListData.size());
            setResult(AgentConstants.ACTIVE_COUNT, agentListData.size() - offLineAgents);
            setResult(AgentConstants.DATA, agentListData);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent list", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String download() {
        //TODO not yet implemented
        setResult(SUCCESS, " no implementation");
        return SUCCESS;
    }


    public String getAgentCount() {
        try {
            FacilioChain chain = ReadOnlyChainFactory.getGenerateCriteriaFromFilterChain();
            FacilioContext context = chain.getContext();
            constructListContext(context);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.AGENT);
            chain.execute();
            Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
            AgentBean agentBean = getAgentBean();
            setResult(AgentConstants.DATA, agentBean.getAgentCount(querySearch,filterCriteria));
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting agentCount->", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }


    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    private Long agentId;


    private Long controllerId;
    private Long deviceId;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    private Integer controllerType;
    
    private String querySearch;
    
    public String getQuerySearch() {
		return querySearch;
	}

	public void setQuerySearch(String querySearch) {
		this.querySearch = querySearch;
	}
    private List<Long> defaultIds;
    public List<Long> getDefaultIds(){return  defaultIds; }
    public void setDefaultIds(List<Long> defaultIds){this.defaultIds = defaultIds;}

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    private String filters;

    public String listPoints() {
        JSONArray pointData = new JSONArray();
        try {
            //List<Point> points = new ArrayList<>();
            List<Point> pointsData = new ArrayList<>();
            FacilioContext context = constructListContext(new FacilioContext());
            if ((controllerId != null) && (controllerId > 0) && (controllerType != null) && (controllerType > 0) && (deviceId == null)) {
                GetPointRequest getPointRequest = new GetPointRequest()
                        .withControllerId(controllerId)
                        .ofType(FacilioControllerType.valueOf(controllerType))
                        .pagination(context);
                pointsData = getPointRequest.getPoints();
            } else {
                if ((controllerId == null) && (deviceId == null)) {
                    GetPointRequest getPointRequest = new GetPointRequest()
                            .pagination(context);
                    pointsData = getPointRequest.getPoints();
                }
            }
            if (!pointsData.isEmpty()) {
                for (Point point : pointsData) {
                    JSONObject object = new JSONObject();
                    object.putAll(point.toJSON());
                    object.put(AgentConstants.POINT, point.getChildJSON());
                    pointData.add(object);
                }
            }
            setResult(AgentConstants.DATA, pointData);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting points", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }


//    public String countDevices() {
//        try {
//            if ((agentId != null) && (agentId > 0)) {
//                //TYPE AND AGENT ID
//                if ((controllerType != null) && (controllerType > 0)) {
//                    setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(Arrays.asList(getAgentId()), FacilioControllerType.valueOf(getControllerType())));
//                }
//                // AGENT ID ALONE
//                else {
//                    setResult(AgentConstants.DATA, FieldDeviceApi.getAgentDeviceCount(Arrays.asList(getAgentId())));
//                }
//            }
//            // TYPE ALONE
//            else if ((controllerType != null) && (controllerType > 0)) {
//                setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(null, FacilioControllerType.valueOf(getControllerType())));
//            }
//            //DEVICE POINT COUNT
//            else {
//                setResult(AgentConstants.DATA, FieldDeviceApi.getDeviceCount());
//            }
//            setResponseCode(HttpURLConnection.HTTP_OK);
//            setResult(AgentConstants.RESULT, SUCCESS);
//        } catch (Exception e) {
//            LOGGER.info("Exception occurred while getting agentDevices count", e);
//            setResult(AgentConstants.RESULT, ERROR);
//            setResult(AgentConstants.EXCEPTION, e.getMessage());
//            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
//        }
//        return SUCCESS;
//    }


    public String PointsCount() {
        try {
            long count = 0;
            if ((controllerId != null) && (controllerId > 0) && (deviceId == null)) {
                count = PointsAPI.getPointsCount(getControllerId(), -1);
            } else if ((deviceId != null) && (deviceId > 0) && (controllerId != null)) {
                count = PointsAPI.getPointsCount(-1, deviceId);
            } else {
                count = PointsAPI.getPointsCount(-1, -1);
            }

            setResult(AgentConstants.DATA, count);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting all point for agent->" + controllerId + " -", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public JSONObject getChildJson() {
        return childJson;
    }

    public void setChildJson(JSONObject childJson) {
        this.childJson = childJson;
    }

    private JSONObject childJson;

    public String getControllerUsingIdentifier() {
        try {
            Long deviceId = getDeviceId();
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .forDevice(deviceId).ofType(FacilioControllerType.MODBUS_IP);
            Controller controller = getControllerRequest.getController();
            JSONObject jsonObject = new JSONObject();
            if (controller != null) {
                jsonObject.putAll(controller.toJSON());
                jsonObject.put(AgentConstants.CHILDJSON, controller.getChildJSON());
            }
            setResult(AgentConstants.DATA, jsonObject);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller", e);
            setResult(AgentConstants.RESULT, new JSONObject());
           internalError();
        }
        return SUCCESS;
    }

    public String getControllerUsingId() {
        try {
            Controller controllers = AgentConstants.getControllerBean().getControllerFromDb(getControllerId());
            setResult(AgentConstants.DATA, controllers.toJSON());
            ok();
        } catch (Exception e) {
            LOGGER.info(" Exception occurred while getting controller data", e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getAgentFilter() {
        try {
            AgentBean agentBean = getAgentBean();
            List<Map<String, Object>> agentFilter = agentBean.getAgentFilter();
            setResult(AgentConstants.DATA, agentFilter);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent filter ", e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getOverview() {
        try {
        	JSONObject result =  AgentUtilV2.getOverview();
        	if(result == null || result.isEmpty()) {
        		setResult(AgentConstants.DATA, new ArrayList<>());
        	}else {
        		setResult(AgentConstants.DATA, result);
        	}
            
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting overview");
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

	public String getAlertsPoints() {
		try {
			if (AccountUtil.getCurrentOrg() != null) {
				JSONArray alertsPoints = AdminAction.getAlertsPointsData(AccountUtil.getCurrentOrg().getDomain());
                JSONArray arr = new JSONArray();
                if(alertsPoints != null && !alertsPoints.isEmpty()) {
                    int dataSize = alertsPoints.size();
                    for(int i=0;i<dataSize;i++){
                        JSONObject addObj = (JSONObject) alertsPoints.get(i);
                        String msg = addObj.get("message").toString();
                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(msg);
                        long arrival=0L;
                        if(json.containsKey("timestamp")){
                            arrival = (long)json.get("timestamp");
                        }
                        addObj.put("timestamp", arrival);
                        addObj.put("id", i+1);
                        arr.add(addObj);
                    }
                    JSONObject lastRecord = (JSONObject) arr.get(arr.size() - 1);
                    long receivedTime = (long) lastRecord.get("arrivalTime");
                    setResult(AgentConstants.LAST_DATA_RECEIVED_TIME, receivedTime);
                }else{
                    LOGGER.error("kafka points data is empty..");
                }
                setResult(AgentConstants.DATA, arr);
				ok();
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred while getting alert points ", e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}
	private InputStream downloadStream;
    public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}


	public String downloadCertificate() {
		try {
			Organization currentOrg = AccountUtil.getCurrentOrg();
            AgentBean agentBean = getAgentBean();
			com.facilio.agentv2.FacilioAgent agent = agentBean.getAgent(agentId);
			Objects.requireNonNull(agent, "no such agent");
			String certFileId = FacilioAgent.getCertFileId("facilio");
			LOGGER.info("certFileId " + certFileId);
			long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getOrgId();
			Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
			long fileId = -1;
			if (orgInfo != null) {
				fileId = Long.parseLong((String) orgInfo.get("value"));
			}
			if (fileId > 0) {
				FileStore fs = FacilioFactory.getFileStore();
				FileInfo fileInfo = fs.getFileInfo(fileId, true);
				if (fileInfo != null) {
					downloadStream = fs.readFile(fileInfo);
					if (downloadStream != null) {
						String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
						setLastModified(dateStamp);
						setDayExpiry();
						String fileName = currentOrg.getDomain()+"_cert.zip";
		    			setFilename(fileName);
					} else {
						throw new Exception("File not Found");
					}
				} else {
					setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
				}
			}
			
			ok();

		} catch (Exception e) {
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
			LOGGER.info("Exception while getting download cert link", e);
		}
		return SUCCESS;
	}

    public String downloadConfig() throws Exception {
        AgentBean agentBean = getAgentBean();
        com.facilio.agentv2.FacilioAgent agent = agentBean.getAgent(agentId);
        String dirPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "agentconfig";
        String path = dirPath + File.separator + agent.getName() + ".config";
        File file = new File(path);
        File dir = new File(dirPath);
        boolean directoryExits = (dir.exists() && dir.isDirectory());
        if (!directoryExits) {
            dir.mkdirs();
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        String content = getFacilioConfig(AccountUtil.getCurrentOrg().getDomain(), agent);
        // Files.write( Paths.get(path), content.getBytes());
        fos.write(content.getBytes());
        fos.close();
        downloadStream = new FileInputStream(path);
        setFilename("facilio.config");
        return SUCCESS;

    }

    private static String getFacilioConfig(String domainName, com.facilio.agentv2.FacilioAgent agent) {
        //TODO: Remove certificate/key path here, once agent(facilio, niagara) changes goes live
        StringBuilder builder = new StringBuilder("clientId=").append(agent.getName()).append(System.lineSeparator())
                .append("privateKeyFile=facilio-private.key").append(System.lineSeparator())
                .append("certificateFile=facilio.crt").append(System.lineSeparator())
                .append("privateKeyName=facilio-private.key").append(System.lineSeparator())
                .append("certName=facilio.crt").append(System.lineSeparator())
                .append("endpoint=").append(FacilioProperties.getIotEndPoint()).append(System.lineSeparator())
                .append("topic=").append(domainName).append(System.lineSeparator());
        AgentType type = AgentType.valueOf(agent.getAgentType());
        if (type == AgentType.FACILIO) {
            String defaultAppDomain = AccountUtil.getDefaultAppDomain();
            if(!defaultAppDomain.startsWith("https://")){
                defaultAppDomain = "https://" + defaultAppDomain;
            }
            builder.append(System.lineSeparator())
                    .append("url="+ defaultAppDomain).append(System.lineSeparator())
                    .append("isBacnetIpEnabled=true").append(System.lineSeparator())
                    .append("isValidateJsonSchemaEnabled=false");
        }
        if (type == AgentType.NIAGARA) {
            builder.append("caFile=root.crt");
        }
        return builder.toString();
    }

	public List<Long> getRecordIds() { return recordIds; }

    public void setRecordIds(List<Long> recordIds) { this.recordIds = recordIds; }

    private List<Long> recordIds;

    public String addClientToPolicy(){
        try{
            Organization currentOrg = AccountUtil.getCurrentOrg();
            if(currentOrg != null){
                AwsUtil.addClientToPolicy(getName(),currentOrg.getDomain(),"facilio");
                setResult(AgentConstants.RESULT,SUCCESS);
                ok();
            }else {
                throw new Exception("current org can't be null");
            }
        }catch (Exception e){
            internalError();
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            LOGGER.info("Exception while adding client to policy ");
        }
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    public String getIotMessage()
    {
        try{
            IotMessage iotMessage = IotMessageApiV2.getIotMessage(id);
            setResult(AgentConstants.DATA,iotMessage.getMessageData());
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting iot message", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }
    //__________________________________________________
    // general utilities

    /*public String addModbusControllers() {
        try {
            if (FacilioProperties.isDevelopment()) {
                ModbusTcpControllerContext controllerContext = new ModbusTcpControllerContext();
                FacilioAgent agent = AgentApiV2.getAgent(agentId);
                Objects.requireNonNull(agent,"Agent can't be null");
                controllerContext.setAgentId(getAgentId());
                controllerContext.setSlaveId(1);
                controllerContext.setIpAddress("1.1.1.1");
                controllerContext.setSiteId(agent.getSiteId());
                controllerContext.setName(controllerContext.getIdentifier());
                FieldDeviceApi.addControllerAsDevice(controllerContext);
                AgentConstants.getControllerBean().addController(controllerContext);
                for (int i = 0; i < 20; i++) {
                    ModbusTcpPointContext modbusTcpPointContext = new ModbusTcpPointContext(agentId,controllerContext.getId());
                    modbusTcpPointContext.setName("point"+i);
                    modbusTcpPointContext.setFunctionCode(i);
                    modbusTcpPointContext.setRegisterNumber(1000+i);
                    modbusTcpPointContext.setModbusDataType(3L);
                    modbusTcpPointContext.setDeviceId(controllerContext.getDeviceId());
                    PointsAPI.addPoint(modbusTcpPointContext);
                }

                BacnetIpControllerContext bc = new BacnetIpControllerContext();
                bc.setName("bacnet controller 1");
                bc.setNetworkNumber(1);
                bc.setIpAddress("1.1.1.1");
                bc.setInstanceNumber(2);
                bc.setAgentId(agentId);
                bc.setSiteId(agent.getSiteId());
                FieldDeviceApi.addControllerAsDevice(bc);
                AgentConstants.getControllerBean().addController(bc);
                for (int i = 0; i < 10; i++) {
                    BacnetIpPointContext bp = new BacnetIpPointContext();
                    bp.setInstanceNumber(i);
                    bp.setDeviceId(bc.getDeviceId());
                    bp.setInstanceType(i);
                    bp.setName("bp"+i);
                    PointsAPI.addPoint(bp);
                }
                ok();
            } else {
                internalError();
            }
        } catch (Exception e) {
            LOGGER.info("Exception while adding device and controller ", e);
            internalError();
        }
        return SUCCESS;
    }*/

    public String retry(){
        try{
            if(id < 0){
                throw new Exception("id cant be less than 1");
            }
            Map<String, Object> anImport = ModbusImportUtils.getImport(getId());
            if(! anImport.isEmpty()){
                if((anImport.containsKey(AgentConstants.STATUS))){
                    long status = ((Number) anImport.get(AgentConstants.STATUS)).longValue();
                    if(status == 0){
                        if(anImport.containsKey(AgentConstants.FILE_ID)){
                            long fileId = ((Number)anImport.get(AgentConstants.FILE_ID)).longValue();
                            long idx = ((Number) anImport.get(AgentConstants.IDX)).longValue();
                            if(fileId > 0){
                                long type = ((Number)anImport.get(AgentConstants.TYPE)).longValue();
                                FileStore fileStore = FacilioFactory.getFileStore();
                                InputStream inputStream = fileStore.readFile(fileId);
                                if(type == ModbusImportUtils.CONTROLLER_IMPORT){
                                    ModbusImportUtils.processFileAndSendAddControllerCommand(idx,inputStream);
                                    ModbusImportUtils.markImportComplete(getId());
                                }else {
                                    ModbusImportUtils.processFileAndSendConfigureModbusPointsCommand(idx,inputStream);
                                    ModbusImportUtils.markImportComplete(getId());
                                }
                            }else {
                                throw new Exception("file is cant be less than 1");
                            }
                        }else {
                            throw new Exception("import data is missing file id");
                        }
                    }else {
                        throw new Exception("this import is processed already");
                    }
                }else {
                    throw new Exception("status missing from import data");
                }
            }else {
                throw new Exception("No such import");
            }
            ok();
        }catch (Exception e){
            LOGGER.info("Exception occurred while retruing import "+getId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String ignoreImport(){
        try{
            ModbusImportUtils.ignoreImport(getId());
            ok();
        }catch (Exception e){
            LOGGER.info("Exception occurred while ignoring import "+getId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
    public String getPolicyGist(){
        try{
            JSONObject policyGist = AwsUtil.getPolicyGist();
            setResult(AgentConstants.DATA,policyGist);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting policy gists",e);
            internalError();
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String listThreadDump(){
        try{
            setResult(AgentConstants.DATA, AgentThreadDumpAPI.getDumps(agentId,constructListContext(new FacilioContext())));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting thread Dump agentId "+getAgentId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
    public String countThreadDump(){
        try{
            setResult(AgentConstants.DATA, AgentThreadDumpAPI.count(agentId));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting thread dump count for agentId "+getAgentId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
    private Boolean latestVersion;
	public Boolean getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(Boolean latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String listAgentVersions(){
        try{
        	FacilioContext context = new FacilioContext();
        	context.put(AgentConstants.IS_LATEST_VERSION, getLatestVersion());
        	setResult(AgentConstants.DATA, FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,()->AgentVersionApi.listAgentVersions(context)));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting versions +",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
	private Boolean disable;
	public Boolean getDisable() {
		return disable;
	}

	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

	public void updateAgentControl() throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
		bean.disableOrEnableAgent(getAgentId(), getDisable());
	}

	@Getter @Setter
    private WorkflowRuleContext workflowRule;
    @Getter @Setter
    private String moduleName;

	public String addAgentWorkflowRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.addAgentWorkflowRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, getWorkflowRule());
        context.put(AgentConstants.AGENT_ID,getAgentId());
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowRule);

	    return SUCCESS;
    }

    public AgentBean getAgentBean() throws InstantiationException, IllegalAccessException {
        return (AgentBean) BeanFactory.lookup("AgentBean");
    }

}
