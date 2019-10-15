package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleFactory;

public class DevicesAction extends FacilioAction{
	private static final long serialVersionUID = 1L;


	private String code;
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DeviceContext getDevice() {
		return device;
	}

	public void setDevice(DeviceContext device) {
		this.device = device;
	}

	private DeviceContext device;
    
    public String deleteDevice() throws Exception
    {
    	FacilioChain deleteDeviceChain = TransactionChainFactory.getDeleteDeviceChain();
		
		FacilioContext context=deleteDeviceChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, device);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(device.getId()));		
		deleteDeviceChain.execute();
		
		List<Long> recordIds=((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		int rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult("rowsUpdated", rowsUpdated);
		
		return SUCCESS;
    }
    
    
    public String disconnectDevice() throws Exception{

    	FacilioChain disconnectDeviceChain = TransactionChainFactory.getDisconnectDeviceChain();

		FacilioContext context=disconnectDeviceChain.getContext();
		setAssetUpdateDefaultsInContext(context);
		setResult("status", "success");
		context.put(FacilioConstants.ContextNames.RECORD, device);
		disconnectDeviceChain.execute();
    	
    	return SUCCESS;
    }
	public String fetchDeviceDetails() throws Exception {
        
        

        DeviceContext deviceDetail = DevicesAPI.getDevice(device.getId());
        setResult(FacilioConstants.ContextNames.DEVICE_DETAILS, deviceDetail);
        return SUCCESS;
    }

    public String fetchDeviceList() throws Exception{
        if (StringUtils.isEmpty(getOrderBy())) {
            setOrderBy("Devices.CONNECTED_TIME");
            setOrderType("DESC");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioContext context = constructListContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
        context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, Collections.singletonList(modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES)));

        FacilioChain fetchList = ReadOnlyChainFactory.fetchModuleDataListChain();
        fetchList.execute(context);

        if (isFetchCount()) {
            setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.RECORD_COUNT));
        }
        else {
            setResult(FacilioConstants.ContextNames.DEVICE_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
        }
        
 		return SUCCESS;
    }
    public String addDevice() throws Exception {
    	
    	FacilioChain addDeviceChain=TransactionChainFactory.addDeviceChain();
    	FacilioContext context = addDeviceChain.getContext();
    	context.put(FacilioConstants.ContextNames.DEVICE_CODE,getCode());
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, getDevice());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		
		device.setCategory(AssetsAPI.getCategory(FacilioConstants.ModuleNames.DEVICES));
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, device.getCategory().getId());
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		addDeviceChain.execute();
		setResult("result",device);		
    	return SUCCESS;
    }
    
	public String getDigitalLogBook() throws Exception {
	    	
//	    	FacilioChain addDeviceChain=TransactionChainFactory.addDeviceChain();
//	    	FacilioContext context = addDeviceChain.getContext();
//	    	context.put(FacilioConstants.ContextNames.DEVICE_CODE,getCode());
//			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
//			context.put(FacilioConstants.ContextNames.RECORD, getDevice());
//			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
//			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
//			
//			device.setCategory(AssetsAPI.getCategory(FacilioConstants.ModuleNames.DEVICES));
//			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, device.getCategory().getId());
//			context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
//			context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
//			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
//			addDeviceChain.execute();
//			setResult("result",device);		
	    	return SUCCESS;
	    }
	    
    
    public String connectDevice() throws Exception{
		
		FacilioChain connectDeviceChain = TransactionChainFactory.connectDeviceChain();
		
    	FacilioContext context = connectDeviceChain.getContext();
    	setAssetUpdateDefaultsInContext(context);
    			
		connectDeviceChain.execute();
		
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;
				
    }
    
    public String updateDevice() throws Exception{
    	FacilioChain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();
		
    	FacilioContext context = updateAssetChain.getContext();
    	setAssetUpdateDefaultsInContext(context);
    			
    	updateAssetChain.execute();
		
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;
    	
    }
    public void setAssetUpdateDefaultsInContext(FacilioContext context)
    {
    	context.put(FacilioConstants.ContextNames.DEVICE_CODE,getCode());
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, device);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(device.getId()));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
		
		// cannot update module state directly
		if (device.getModuleState() != null) {
			device.setModuleState(null);
		}
		//context.put(FacilioConstants.ContextNames.TRANSITION_ID, null);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
    }
    
}
