package com.facilio.multiImport.command;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FilterAndImportOneLevelRecordsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FilterAndImportOneLevelRecordsCommand.class.getName());

    Context context;
    String moduleName;
    Map<Long, ModuleBaseWithCustomFields> logIdVsOldLookupRecord;
    List<FacilioField> mappedFields;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context=context;

        moduleName = Constants.getModuleName(context);
        List<ImportRowContext> allRows = ImportConstants.getRowContextList(context);

        logIdVsOldLookupRecord = (Map<Long, ModuleBaseWithCustomFields>) context.get(MultiImportApi.ImportProcessConstants.LOG_ID_VS_OLD_RECORD_MAP);
        mappedFields = ImportConstants.getMappedFields(context);
        Map<Long, ImportRowContext> logIdVsRowContext = allRows.stream().collect(Collectors.toMap(ImportRowContext::getId, Function.identity()));
        ImportConstants.setLogIdVsRowContextMap(context, logIdVsRowContext);
        List<Pair<Long, Map<String, Object>>> rawInputs = MultiImportApi.getErrorLessRecords(allRows);

        List<Pair<Long, Map<String, Object>>> updateInputs = new ArrayList<>();
        List<Pair<Long, Map<String, Object>>> newCreateInputs = new ArrayList<>();

        for(Pair<Long,Map<String,Object>> datum : rawInputs){
            Long logId = datum.getKey();
            Map<String,Object> record = datum.getValue();
            ModuleBaseWithCustomFields dbRecord = getDBRecord(logId);
            if(dbRecord != null){
                record.put("id",dbRecord.getId());
                updateInputs.add(datum);
            }else{
                newCreateInputs.add(datum);
            }

        }


        context.put(Constants.PATCH_FIELDS,mappedFields);

        ImportConstants.setInsertRecords(context, newCreateInputs);
        ImportConstants.setUpdateRecords(context,updateInputs);
        ImportConstants.setOldRecordsMap(context,logIdVsOldLookupRecord);

        MultiImportSetting setting = null;
        if(CollectionUtils.isNotEmpty(updateInputs) && CollectionUtils.isNotEmpty(newCreateInputs)){
            setting = MultiImportSetting.BOTH;
        } else if (CollectionUtils.isNotEmpty(newCreateInputs)) {
            setting = MultiImportSetting.INSERT;
        } else if (CollectionUtils.isNotEmpty(updateInputs)) {
            setting = MultiImportSetting.UPDATE;
        }

        importOneLevelRecords(setting);

        return false;
    }
    private void importOneLevelRecords(MultiImportSetting setting) throws Exception {
        if(setting == null){
            return;
        }
        LOGGER.info("ONE LEVEL IMPORT STARTED FOR MODULE NAME :"+moduleName+" START TIME:"+System.currentTimeMillis());

        FacilioChain importChain = FacilioChain.getTransactionChain();
        MultiImportChainUtil.addCreateAndPatchChainsBySettings(importChain,setting,moduleName,true);

        FacilioContext importContext = importChain.getContext();
        importContext.putAll(context);

        importChain.execute();

        ImportConstants.setInsertRecordMap(context, ImportConstants.getInsertRecordMap(importContext));
        ImportConstants.setUpdateRecordMap(context,  ImportConstants.getUpdateRecordMap(importContext));

        LOGGER.info("ONE LEVEL IMPORT COMPLETED FOR MODULE NAME :"+moduleName+" COMPLETED TIME:"+System.currentTimeMillis());

    }
    protected ModuleBaseWithCustomFields getDBRecord(Long currentLogId) {
        if(MapUtils.isEmpty(logIdVsOldLookupRecord)){
            return null;
        }
        return logIdVsOldLookupRecord.get(currentLogId);
    }
}
