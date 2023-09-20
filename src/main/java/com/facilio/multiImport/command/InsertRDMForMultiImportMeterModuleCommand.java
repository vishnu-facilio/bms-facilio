package com.facilio.multiImport.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class InsertRDMForMultiImportMeterModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("InsertRDMForMultiImportMeterModuleCommand start time:"+System.currentTimeMillis());
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        
        List<V3MeterContext> meters = new ArrayList<>();

        for(ModuleBaseWithCustomFields record : records){
        	V3MeterContext meter = (V3MeterContext) record;
        	meters.add(meter);
        }

        ReadingsAPI.updateReadingDataMetaForMeters(meters);

        LOGGER.info("InsertRDMForMultiImportMeterModuleCommand end time:"+System.currentTimeMillis());
        return false;
    }
}
