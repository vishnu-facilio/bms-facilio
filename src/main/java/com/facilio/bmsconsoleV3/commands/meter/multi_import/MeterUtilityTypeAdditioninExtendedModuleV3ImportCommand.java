package com.facilio.bmsconsoleV3.commands.meter.multi_import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;

public class MeterUtilityTypeAdditioninExtendedModuleV3ImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        List<Pair<Long,ModuleBaseWithCustomFields>> meterList = insertRecordMap.get(FacilioConstants.Meter.METER);

        if(CollectionUtils.isEmpty(meterList)){
            return false;
        }

        ModuleBean modBean = Constants.getModBean();

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsMeterPair : meterList) {
            Long logId = logIdVsMeterPair.getKey();
            try{
            	V3MeterContext meter =(V3MeterContext)logIdVsMeterPair.getValue();
            	V3UtilityTypeContext utilityType = meter.getUtilityType();
                long meterModuleId = utilityType.getMeterModuleID();
                FacilioModule module = modBean.getModule(meterModuleId);
                if(insertRecordMap.containsKey(module.getName())){
                    List<Pair<Long,ModuleBaseWithCustomFields>> list = insertRecordMap.get(module.getName());
                    list.add(logIdVsMeterPair);
                    insertRecordMap.put(module.getName(),list);
                }else{
                    insertRecordMap.put(module.getName(), new ArrayList<>(Arrays.asList(logIdVsMeterPair)));
                }
                if (meter.getMeterLocation() == null || meter.getMeterLocation().getId() < 0) {
                    V3BaseSpaceContext meterLocation = new V3BaseSpaceContext();
                    meterLocation.setId(meter.getSiteId());
                    meter.setMeterLocation(meterLocation);
                }
            }catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                if(StringUtils.isNotEmpty(e.getMessage())) {
                    importRowContext.setErrorMessage(e.getMessage());
                }else {
                    importRowContext.setErrorMessage(e.toString());
                }
            }
        }
        Set<String> extendedModules = new HashSet<>(insertRecordMap.keySet());
        extendedModules.remove(FacilioConstants.Meter.METER);
        Constants.setExtendedModules(context, extendedModules);
        return false;
    }
}
