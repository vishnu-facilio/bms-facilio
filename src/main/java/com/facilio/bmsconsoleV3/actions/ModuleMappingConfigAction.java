package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ModuleMappingConfigAction extends V3Action {

    private String sourceModule;
    private String targetModule;
    private long templateId;
    private long recordId;
    private JSONObject record;
    private String templateName;
    private JSONObject targetValue;
    private ArrayList<Long> recordIds;
    private int conversionType;
    private boolean viewOnly;

    public String fetchTargetModuleContext() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.fetchModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME, templateName);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD, record);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD, targetValue);

        chain.execute();

        setData(targetModule, context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }

    public String addContextToTargetModule() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME, templateName);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD, record);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD, targetValue);
        context.put(FacilioConstants.ContextNames.ModuleMapping.VIEW_ONLY, viewOnly);

        chain.execute();

        setData((String) context.get(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE), context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }

    public String addOrFetchMultiRecordContext() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrFetchMultiRecordModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME, templateName);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_IDS, recordIds);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD, targetValue);
        context.put(FacilioConstants.ContextNames.ModuleMapping.VIEW_ONLY, viewOnly);

        if (conversionType <= 0) {
            conversionType = ModuleMappings.Conversion_Type.MANY_TO_MANY.getType();
        }

        context.put(FacilioConstants.ContextNames.ModuleMapping.CONVERSION_TYPE, conversionType);


        chain.execute();

        setData((String) context.get(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE), context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }

    public String addOrFetchSingleRecordContext() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrFetchMultiRecordModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME, templateName);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_IDS, recordIds);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD, targetValue);
        context.put(FacilioConstants.ContextNames.ModuleMapping.VIEW_ONLY, viewOnly);

        if (conversionType <= 0) {
            conversionType = ModuleMappings.Conversion_Type.MANY_TO_ONE.getType();
        }

        context.put(FacilioConstants.ContextNames.ModuleMapping.CONVERSION_TYPE, conversionType);


        chain.execute();

        setData((String) context.get(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE), context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }

}
