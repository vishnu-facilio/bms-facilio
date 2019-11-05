package com.facilio.agentnew.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.device.Device;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class AddDevicesCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddDevicesCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(context.containsKey(AgentConstants.DATA)){
            FacilioContext deviceContex = new FacilioContext();
            List<Device> devices = (List<Device>) context.get(AgentConstants.DATA);
            ModuleCRUDBean bean;
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",  AccountUtil.getCurrentOrg().getOrgId());
            for (Device device : devices) {
                deviceContex.put(FacilioConstants.ContextNames.TO_INSERT_MAP, FieldUtil.getAsJSON(device));
                deviceContex.put(FacilioConstants.ContextNames.TABLE_NAME, ModuleFactory.getFieldDeviceModule().getTableName());
                deviceContex.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getFieldDeviceFields());
                Long deviceId = bean.genericInsert(deviceContex);
                if( deviceId > 0){
                    LOGGER.info("added device " + deviceId);
                }else {
                    LOGGER.info(" Exception while adding device " + device);
                }
            }
            return true;
        }else {
            LOGGER.info("Exception occurred context might not contain data or orgid ");
        }
        return false;
    }
}
