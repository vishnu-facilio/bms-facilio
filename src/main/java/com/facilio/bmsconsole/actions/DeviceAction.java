package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

public class DeviceAction extends ActionSupport
{
	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(DeviceAction.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DeviceAction.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(DeviceAction.class.getName());

	private static final String RESULT = "result";

	private String url;

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	private String subscription;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getMessage() {
		return message;
	}

	public void setMessage(JSONObject message) {
		this.message = message;
	}



	private JSONObject message;

    public JSONObject getControllerData() {
        return controllerData;
    }

    public void setControllerData(JSONObject controllerData) {
        this.controllerData = controllerData;
    }

    private JSONObject controllerData;


	public String pubSubListener(){
		LOGGER.info("PubSub Called");
		if(getMessage()== null) {
			return "message is null";
		}
		if (getSubscription() == null){
			return "subscription is null";
		}

		LOGGER.info("message : "+ getMessage() );
		LOGGER.info("subscription : "+getMessage());

		return SUCCESS;
	}



//	public String downloadCertificate() {
//		String clientIdAndPolicyName = AccountUtil.getCurrentOrg().getDomain();
//		url = DownloadCertFile.downloadCertificate( clientIdAndPolicyName , AgentType.BACnet.getLabel());
//		return SUCCESS;
//	}



	private long assetCategoryId;

    public long getAssetCategoryId() {
        return assetCategoryId;
    }

    public void setAssetCategoryId(long assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public int getControllerType() {
        return controllerType;
    }

    public void setControllerType(int controllerType) {
        this.controllerType = controllerType;
    }

    private int controllerType;

    private long fieldId;


    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private JSONObject jsonObject;

	public String getJSON(){
	    LOGGER.info("--------- json ------"+getJsonObject());
	    return SUCCESS;
    }

	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	/*public static void main(String[] args) {
		DeviceAction deviceAction = new DeviceAction();
		deviceAction.getAgentUsingId();
	}*/

	private Long agentId;
	public Long getAgentId() { return agentId; }
	public void setAgentId( Long agentId) {
		this.agentId = agentId;
	}






	public Long getControllerId() {
		return controllerId;
	}

	public void setControllerId(Long controllerId) {
		this.controllerId = controllerId;
	}

	Long controllerId;






	private long pointId;
	public long getPointId() { return pointId; }
	public void setPointId(long pointId) { this.pointId = pointId; }











	boolean checkValue(Long value){
		return (value != null) && (value >  0);
	}

	private JSONObject result;
	public JSONObject getResult() {
		return result;
	}

	@SuppressWarnings("unchecked")
	public void setResult(String key, Object result) {
		if (this.result == null) {
			this.result = new JSONObject();
		}
		this.result.put(key, result);
	}

	Set<String> exceptions;




//	public String show()
//	{
//		try 
//		{
//			ValueStack stack = ActionContext.getContext().getValueStack();
//		    Map<String, Object> context = new HashMap<String, Object>();
//
//		    context.put("DEVICES", DeviceAPI.getAllControllers(OrgInfo.getCurrentOrgInfo().getOrgid())); 
//		    stack.push(context);
//		} 
//		catch (SQLException e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
//			return ERROR;
//		}
//		return SUCCESS;
//	}
//	
//	public String showAdd()
//	{
//		return SUCCESS;
//	}
//	
//	public void add()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String name = request.getParameter("controllerName");
//		Integer type = Integer.parseInt(request.getParameter("controllerType"));
//		String datasource = request.getParameter("datasource");
//		int timeinterval = Integer.parseInt(request.getParameter("timeinterval"));
//		
//		try 
//		{
//			Long controllerId = AssetsAPI.addAsset(name, OrgInfo.getCurrentOrgInfo().getOrgid());
//			if(type == 1)
//			{
//				String publicip = request.getParameter("publicip");
//				FacilioTimer.schedulePeriodicJob(controllerId, "DeviceDataExtractor", 15, timeinterval, "facilio");
//				DeviceAPI.addController(controllerId, type, publicip, timeinterval, null);
//				DeviceAPI.discoverDevices(controllerId, OrgInfo.getCurrentOrgInfo().getOrgid());
//			}
//			else
//			{
//				DeviceAPI.addController(controllerId, type, null, timeinterval, null);
//			}
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
//		}
//	}
//	
////	public void addDevice()
////	{
////		HttpServletRequest request = ServletActionContext.getRequest();
////		String name = request.getParameter("deviceName");
////		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
////		try 
////		{
////			Long deviceId = AssetsAPI.getAssetId(name, OrgInfo.getCurrentOrgInfo().getOrgid());
////			if(deviceId == null)
////			{
////				deviceId = AssetsAPI.addAsset(name, OrgInfo.getCurrentOrgInfo().getOrgid());
////				DeviceAPI.addDevice(deviceId, null, null, null, controllerId, null, 1, 1);
////			}
////		}
////		catch (Exception e) 
////		{
////			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
////		}
////	}
//	
//	public void rearrangeDevices()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		Long deviceId = Long.parseLong(request.getParameter("id"));
//		Long parentDeviceId = Long.parseLong(request.getParameter("parent"));
//		try 
//		{
//			DeviceAPI.updateDeviceParent(deviceId, parentDeviceId, controllerId);
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while rearrangeDevice" +e.getMessage(), e);
//		}
//	}
//	
//	public void updateControllerInstances()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		try 
//		{
//			JSONParser parser = new JSONParser();
//			JSONArray instances = (JSONArray) parser.parse(request.getParameter("instances"));
//			DeviceAPI.updateControllerInstances(request.getParameter("deviceId") != null?Long.parseLong(request.getParameter("deviceId")):null, instances, controllerId);
//			if((Integer)DeviceAPI.getControllerInfo(controllerId).get("status") == 2)
//	    	{
//	    		DeviceAPI.updateControllerStatus(controllerId, 4);
//	    	}
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
//		}
//	}
//	
//	public void enableDeviceMonitoring()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
//		int polltime = 30;//Integer.parseInt(request.getParameter("polltime"));
//		try 
//		{
//			FacilioTimer.schedulePeriodicJob(deviceId, "DeviceDataExtractor", 15, polltime, "facilio");
////			DeviceAPI.updateController(deviceId, jobId, true);
//			
//			FacilioTimer.schedulePeriodicJob(deviceId, "IotConnector", 15, 20, "facilio");
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while enableDeviceMonitoring" +e.getMessage(), e);
//		}
//	}
//	
//	public void disableDeviceMonitoring()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
//		try 
//		{
//			DeviceAPI.updateController(deviceId, null, false);
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while disableDeviceMonitoring" +e.getMessage(), e);
//		}
//	}
//	
//	public String showDeviceInfo() {
//		
//		HttpServletRequest request = ServletActionContext.getRequest();
//		String reqURI = request.getRequestURI();
//		
//		try 
//		{
//			Long controllerId = Long.parseLong(reqURI.substring(reqURI.lastIndexOf("/")+1));
//			
//			ValueStack stack = ActionContext.getContext().getValueStack();
//		    Map<String, Object> context = new HashMap<String, Object>();
//
//		    List<Map<String, Object>> unmodelledInstances = new ArrayList<>();
//		    Map<Long, List<Map<String, Object>>> modelledInstances = new HashMap<>();
//		    Map<String, Map<String, Object>> controllerInstances = DeviceAPI.getControllerInstances(controllerId);
//		    Iterator<String> keys = controllerInstances.keySet().iterator();
//		    while(keys.hasNext())
//		    {
//		    	String key = keys.next();
//		    	Map<String, Object> instance = controllerInstances.get(key);
//		    	if((Long)instance.get("deviceId") == 0L)
//		    	{
//		    		unmodelledInstances.add(instance);
//		    	}
//		    	else
//		    	{
//		    		List<Map<String, Object>> instanceList = new ArrayList<>();
//		    		if(modelledInstances.containsKey((Long)instance.get("deviceId")))
//		    		{
//		    			instanceList = modelledInstances.get((Long)instance.get("deviceId"));
//		    		}
//		    		else
//		    		{
//		    			instanceList = new ArrayList<>();
//		    		}
//		    		instanceList.add(instance);
//		    		modelledInstances.put((Long)instance.get("deviceId"), instanceList);
//		    	}
//		    }
//		    Map<Long, Device> devices = DeviceAPI.getDevices(controllerId);
//		    Iterator<Long> deviceIterator = devices.keySet().iterator();
//		    while(deviceIterator.hasNext())
//		    {
//		    	Long deviceId = deviceIterator.next();
//		    	Device device = devices.get(deviceId);
//		    	if(modelledInstances.containsKey(deviceId))
//		    	{
//		    		for(Map<String, Object> instanceMap: modelledInstances.get(deviceId))
//		    		{
//		    			device.addInstance((Long) instanceMap.get("controllerInstanceId"), (String) instanceMap.get("instanceName"));
//		    		}
//		    	}
//		    }
//		    
//		    request.setAttribute("CONTROLLER_ID", controllerId);
//		    context.put("INSTANCES", unmodelledInstances);
//		    context.put("DEVICES", devices);
//		    
//		    context.put("CONTROLLER_ID", controllerId);
//		    context.put("CONTROLLER_INFO", DeviceAPI.getControllerInfo(controllerId));
//		    stack.push(context);
//		} 
//		catch (SQLException e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
//			return ERROR;
//		}
//		return SUCCESS;
//	}
//	
//	public void showDeviceData()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		//Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		HttpServletResponse response = ServletActionContext.getResponse();
//		try 
//		{
//			System.out.println(DeviceAPI.getAllControllers(OrgInfo.getCurrentOrgInfo().getOrgid()));
//			Long controllerId = (Long) (DeviceAPI.getAllControllers(OrgInfo.getCurrentOrgInfo().getOrgid())).get(0).get("id");
//			
//			response.setContentType("application/json");
//		    response.setCharacterEncoding("UTF-8");
//		    Long deviceId = DeviceAPI.getDeviceId(controllerId);
//		    response.getWriter().print(DeviceAPI.getDeviceData(deviceId).toString());
//		} 
//		catch (Exception e) 
//		{
//			//logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
//		}
//	}
//	
//	public String showDevices()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		try 
//		{
//			ValueStack stack = ActionContext.getContext().getValueStack();
//		    Map<String, Object> context = new HashMap<String, Object>();
//
//		    List<Map<String, Object>> unmodelledInstances = new ArrayList<>();
//		    Map<Long, List<Map<String, Object>>> modelledInstances = new HashMap<>();
//		    Map<String, Map<String, Object>> controllerInstances = DeviceAPI.getControllerInstances(controllerId);
//		    Iterator<String> keys = controllerInstances.keySet().iterator();
//		    while(keys.hasNext())
//		    {
//		    	String key = keys.next();
//		    	Map<String, Object> instance = controllerInstances.get(key);
//		    	if((Long)instance.get("deviceId") == 0L)
//		    	{
//		    		unmodelledInstances.add(instance);
//		    	}
//		    	else
//		    	{
//		    		List<Map<String, Object>> instanceList = new ArrayList<>();
//		    		if(modelledInstances.containsKey((Long)instance.get("deviceId")))
//		    		{
//		    			instanceList = modelledInstances.get((Long)instance.get("deviceId"));
//		    		}
//		    		else
//		    		{
//		    			instanceList = new ArrayList<>();
//		    		}
//		    		instanceList.add(instance);
//		    		modelledInstances.put((Long)instance.get("deviceId"), instanceList);
//		    	}
//		    }
//		    Map<Long, Device> devices = DeviceAPI.getDevices(controllerId);
//		    Iterator<Long> deviceIterator = devices.keySet().iterator();
//		    while(deviceIterator.hasNext())
//		    {
//		    	Long deviceId = deviceIterator.next();
//		    	Device device = devices.get(deviceId);
//		    	if(modelledInstances.containsKey(deviceId))
//		    	{
//		    		for(Map<String, Object> instanceMap: modelledInstances.get(deviceId))
//		    		{
//		    			device.addInstance((Long) instanceMap.get("controllerInstanceId"), (String) instanceMap.get("instanceName"));
//		    		}
//		    	}
//		    }
//		    context.put("INSTANCES", unmodelledInstances);
//		    context.put("DEVICES", devices);
//		    context.put("CONTROLLERID", controllerId);
//		    stack.push(context);
//		} 
//		catch (SQLException e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
//			return ERROR;
//		}
//		return SUCCESS;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void showTree()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		HttpServletResponse response = ServletActionContext.getResponse();
//		try 
//		{
//			JSONObject result = new JSONObject();
//			JSONArray childArray = new JSONArray();
//			result.put("name", AssetsAPI.getAssetInfo(controllerId).getName());
//			Map<Long, Device> devices = DeviceAPI.getDevices(controllerId);
//			System.out.println(devices);
//			List<Device> deviceTree = new ArrayList<>();
//			for(Device device : devices.values())
//			{
//				if(device.getParentId() != null && devices.containsKey(device.getParentId()))
//				{
//					Device parentDevice = devices.get(device.getParentId());
//					parentDevice.add(device);
//				}
//				else
//				{
//					deviceTree.add(device);
//				}
//			}
//			System.out.println(new Gson().toJson(deviceTree));
//			JSONParser parser = new JSONParser();
//			childArray = (JSONArray) parser.parse(new Gson().toJson(deviceTree));
//			result.put("children", childArray);
//			result.put("id", controllerId);
//			response.setContentType("application/json");
//		    response.setCharacterEncoding("UTF-8");
//		    response.getWriter().print(result);
//		} 
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
//		}
//	}
//	
//	public String showConfigureDevice()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
//		try 
//		{
//			ValueStack stack = ActionContext.getContext().getValueStack();
//		    Map<String, Object> context = new HashMap<String, Object>();
//
//		    context.put("DEVICEINSTANCES", DeviceAPI.getDeviceInstances(deviceId));
//		    context.put("ATTRIBUTES", DeviceAPI.getAttributes());
//		    context.put("DEVICEID", deviceId);
//		    stack.push(context);
//		} 
//		catch (SQLException e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
//			return ERROR;
//		}
//		return SUCCESS;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void configureDevice()
//	{
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
//		System.out.println(request.getParameter("data"));
//		HttpServletResponse response = ServletActionContext.getResponse();
//		try 
//		{
//			JSONParser parser = new JSONParser();
//			JSONObject data = (JSONObject) parser.parse(request.getParameter("data"));
//			Iterator<String> dataIterator = data.keySet().iterator();
//			while(dataIterator.hasNext())
//			{
//				String key = dataIterator.next();
//				String columnName = (String) data.get(key);
//				DeviceAPI.updateDeviceInstance(deviceId, Integer.parseInt(key), columnName);
//			}
//			//DeviceAPI.updateDevice(deviceId, 2);
//			JSONObject result = new JSONObject();
//			result.put("message", "success");
//			response.setContentType("application/json");
//		    response.setCharacterEncoding("UTF-8");
//		    response.getWriter().print(result);
//		} 
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
//		}
//	}
//	
//	public void downloadAgent()
//	{
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/zip");
//		response.setHeader("Content-Disposition","attachment;filename=facilioagent.zip");
//		
//		HttpServletRequest request = ServletActionContext.getRequest();
//		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
//		ZipOutputStream out = null;
//		try 
//		{
//			out = new ZipOutputStream(response.getOutputStream());
//			
//			out.putNextEntry(new ZipEntry("facilioagent/gru.config"));
//			String config = "Port=47808\n"
//					+ "LocalInterface=192.168.0.39\n"
//					+ "BroadcastAddress=255.255.255.255\n"
//					+ "AdpuTimeout=6000\n"
//					+ "ControllerIPAddress=192.168.0.148\n"
//					+ "Datatypes=2\n"
//					+ "ControllerId=" + controllerId + "\n"
//					+ "UserName=" + UserInfo.getCurrentUser().getEmail() + "\n"
//					+ "Password=\n"
//					+ "ClientId=" + AwsUtil.getConfig("environment") + "-" + OrgInfo.getCurrentOrgInfo().getOrgid() + "-" + controllerId;
//			out.write(config.getBytes(), 0, config.getBytes().length);
//			out.closeEntry();
//			
//			byte[] buffer = new byte[1024];
//			FileInputStream fin = new FileInputStream(new File(AwsUtil.class.getClassLoader().getResource("conf/deviceconnector.js").getFile()));
//			out.putNextEntry(new ZipEntry("facilioagent/deviceconnector.js"));
//			int length;
//            while((length = fin.read(buffer)) > 0)
//            {
//                out.write(buffer, 0, length);
//            }
//            out.closeEntry();
//            fin.close();
//            
//            byte[] buffer2 = new byte[1024];
//			FileInputStream fin2 = new FileInputStream(new File(AwsUtil.class.getClassLoader().getResource("conf/run.sh").getFile()));
//			out.putNextEntry(new ZipEntry("facilioagent/run.sh"));
//			int length2;
//            while((length2 = fin2.read(buffer2)) > 0)
//            {
//                out.write(buffer2, 0, length2);
//            }
//            out.closeEntry();
//            fin2.close();
//		} 
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exception while downloadAgent" + e.getMessage(), e);
//		}
//		finally
//		{
//	        try 
//	        {
//	        	out.flush();
//				out.close();
//			} 
//	        catch (IOException e) 
//	        {
//	        	logger.log(Level.SEVERE, "Exception while closing stream" + e.getMessage(), e);
//			}
//		}
//	}
//	public String getDevices() throws Exception 
//	{
//		System.out.println("::::GetDevices");
//		List<Device> allDevice=DeviceAPI.getDevices();
//		setDeviceList(allDevice);
//		//setEnergyContsant();
//		return SUCCESS;
//	}
//	
//	private List<Device> deviceList = null;
//	public List<Device> getDeviceList() {
//		return deviceList;
//	}
//	public void setDeviceList(List<Device> deviceList) {
//		this.deviceList = deviceList;
//	}
//	
//	private Device device;
//	public Device getDevice() {
//		return device;
//	}
//	public void setDevice(Device device) {
//		this.device = device;
//	}
//
//	public String addDevice() throws Exception 
//	{
//		DeviceAPI.addDevice(device);
//		return SUCCESS;
//	}
//	
//	public String deviceList() throws Exception {
//		
//		setDeviceList(DeviceAPI.getDevices());
//		return SUCCESS;
//	}
//	
//	private Map<String, Integer> energyContsant;
//	
//	public void setEnergyContsant(Map<String, Integer> energyContsant) {
//		this.energyContsant = energyContsant;
//	}
//	
//	public Map<String, Integer> getEnergyConstant()
//    {
//		//System.out.println(FieldUtil.<Energy>getAsProperties(Energy));
//    		return Energy.Energy_Data;
//    }
//	public Map<String, Integer> getDateFilter()
//    {
//		//System.out.println(FieldUtil.<Energy>getAsProperties(Energy));
//    		return Reports.DateFilter;
//    }


}

