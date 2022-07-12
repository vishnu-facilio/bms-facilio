package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkToolAdditionCommandV3 extends FacilioCommand {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3ToolContext> toolsList = (List<V3ToolContext>) context.get(FacilioConstants.ContextNames.TOOLS);
        if (toolsList != null && !toolsList.isEmpty()) {
            int size = 0;
            long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
            List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
            Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);
            List<Long> toolTypesId = new ArrayList<>();
            Map<Long, Long> toolTypeVsTool = new HashMap<>();
            SelectRecordsBuilder<V3ToolContext> toolselectBuilder = new SelectRecordsBuilder<V3ToolContext>()
                    .select(toolFields).table(toolModule.getTableName()).moduleName(toolModule.getName())
                    .beanClass(V3ToolContext.class).andCondition(CriteriaAPI.getCondition(toolFieldMap.get("storeRoom"),
                            String.valueOf(storeRoomId), NumberOperators.EQUALS));

            List<V3ToolContext> tools = toolselectBuilder.get();
            if (tools != null && !tools.isEmpty()) {
                for (V3ToolContext tool : tools) {
                    toolTypesId.add(tool.getToolType().getId());
                    toolTypeVsTool.put(tool.getToolType().getId(), tool.getId());
                }
            }

            List<V3ToolContext> toolToBeAdded = new ArrayList<>();
            for (V3ToolContext tool : toolsList) {
                tool.setLastPurchasedDate(System.currentTimeMillis());
                if (!toolTypesId.contains(tool.getToolType().getId())) {
                    toolToBeAdded.add(tool);
                } else {
                    tool.setId(toolTypeVsTool.get(tool.getToolType().getId()));
                    updateTool(toolModule, toolFields, tool);
                    size += 1;
                }
            }

            if (toolToBeAdded != null && !toolToBeAdded.isEmpty()) {
                size += addTool(toolModule, toolFields, toolToBeAdded);
            }

            List<Long> toolIds = new ArrayList<>();
            List<Long> toolTypesIds = new ArrayList<>();
            List<V3PurchasedToolContext> purchasedTools = new ArrayList<>();
            Map<Long, List<V3PurchasedToolContext>> toolVsPurchaseTool = new HashMap<>();
            for (V3ToolContext tool : toolsList) {
                tool.setToolType(V3ToolsApi.getToolTypes(tool.getToolType().getId()));
                toolIds.add(tool.getId());
                toolTypesIds.add(tool.getToolType().getId());
                List<V3PurchasedToolContext> pTools = new ArrayList<>();
                if (tool.getPurchasedTools() != null && !tool.getPurchasedTools().isEmpty()) {
                    for (V3PurchasedToolContext pTool : tool.getPurchasedTools()) {
                        pTool.setTool(tool);
                        pTool.setToolType(tool.getToolType());
                        pTool.setCostDate(System.currentTimeMillis());
                        pTools.add(pTool);
                        purchasedTools.add(pTool);
                    }
                    tool.setPurchasedTools(null);
                    toolVsPurchaseTool.put(tool.getId(), pTools);
                }
            }
            if (purchasedTools != null && !purchasedTools.isEmpty()) {
                size += addPurchasedTool(purchasedTools);
            }

            setImportProcessContext(context, size);

            context.put(FacilioConstants.ContextNames.RECORD_LIST, toolsList);
            context.put(FacilioConstants.ContextNames.TOOL_IDS, toolIds);
            context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
            context.put(FacilioConstants.ContextNames.TOOL_TYPES_IDS, toolTypesIds);
            context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, toolVsPurchaseTool);
        }
        return false;
    }

    private int addTool(FacilioModule module, List<FacilioField> fields, List<V3ToolContext> tool) throws Exception {
        InsertRecordBuilder<V3ToolContext> readingBuilder = new InsertRecordBuilder<V3ToolContext>().module(module)
                .fields(fields).addRecords(tool);
        readingBuilder.save();
        return readingBuilder.getRecords().size();
    }

    private void updateTool(FacilioModule module, List<FacilioField> fields, V3ToolContext tool) throws Exception {
        UpdateRecordBuilder<V3ToolContext> updateBuilder = new UpdateRecordBuilder<V3ToolContext>().module(module)
                .fields(fields).andCondition(CriteriaAPI.getIdCondition(tool.getId(), module));
        updateBuilder.update(tool);
    }

    private int addPurchasedTool(List<V3PurchasedToolContext> tool) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);
        InsertRecordBuilder<V3PurchasedToolContext> readingBuilder = new InsertRecordBuilder<V3PurchasedToolContext>()
                .module(module).fields(fields).addRecords(tool);
        readingBuilder.save();
        return readingBuilder.getRecords().size();
    }

    private void setImportProcessContext(Context c, int size) throws ParseException {
        ImportProcessContext importProcessContext = (ImportProcessContext) c
                .get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        if (importProcessContext != null) {
            JSONObject meta = new JSONObject();
            if (!importProcessContext.getImportJobMetaJson().isEmpty()) {
                meta = importProcessContext.getFieldMappingJSON();
                meta.put("Inserted", size + "");
            } else {
                meta.put("Inserted", size + "");
            }
            importProcessContext.setImportJobMeta(meta.toJSONString());
        }

    }
}
