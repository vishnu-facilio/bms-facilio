package com.facilio.bmsconsole.imports.config;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.imports.annotations.RowFunction;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class V3ProcessImportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        HashMap<String, List<ReadingContext>> groupedContext = new HashMap<String, List<ReadingContext>>();
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isEmpty(moduleName)) {
            moduleName = importProcessContext.getModuleName();
        }
        if (StringUtils.isEmpty(moduleName)) {
            throw new IllegalArgumentException("Module name is empty");
        }

        JSONObject importMeta = importProcessContext.getImportJobMetaJson();
        RowFunction rowFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.UNIQUE_FUNCTION);

        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());

        List<SiteContext> sites = SpaceAPI.getAllSites();
        Map<String, SiteContext> sitesMap = null;
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity()));
        }

        for(Map<String, Object> row: allRows) {
            ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);

            ImportRowContext rowContext;
            if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()){
                rowContext = rowLogContext.getRowContexts().get(0);
            }
            else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.RESOLVED.getValue()) {
                rowContext = rowLogContext.getCorrectedRow();
            } else {
                continue;
            }

            int rowNo = rowContext.getRowNumber();
            HashMap<String, Object> colVal = rowContext.getColVal();

            HashMap<String, Object> props = new LinkedHashMap<String, Object>();

            // adding source_type and sourcce_id in the props
            props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
            props.put(FacilioConstants.ContextNames.SOURCE_ID, importProcessContext.getId());

            if (fieldMapping.containsKey(moduleName + "__formId") && colVal.get(fieldMapping.get(moduleName + "__formId")) != null) {
                props.put("formId", colVal.get(fieldMapping.get(moduleName + "__formId")));
            }

            if (!(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue())) {
                if (MapUtils.isNotEmpty(sitesMap)) {
                    if (colVal.containsKey(fieldMapping.get(moduleName + "__site"))) {
                        String siteName = (String) colVal.get(fieldMapping.get(moduleName + "__site"));
                        SiteContext siteContext = sitesMap.get(siteName.trim().toLowerCase());
                        props.put("siteId", siteContext.getId());
                    }
                }
            }

            rowFunction.apply(rowNo, colVal, context);
        }

        return false;
    }
}
