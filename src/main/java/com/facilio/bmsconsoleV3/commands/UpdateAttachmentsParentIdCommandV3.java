package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateAttachmentsParentIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3Context> records = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(CollectionUtils.isNotEmpty(records)) {
            for(V3Context record : records){
                FacilioModule attachmentModule = null;
                if (StringUtils.isEmpty(attachmentModuleName)) {
                    List<FacilioModule> subModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.ATTACHMENTS);
                    if (CollectionUtils.isNotEmpty(subModules)) {
                        attachmentModule = subModules.get(0);
                        attachmentModuleName = attachmentModule.getName();
                    }
                } else {
                    attachmentModule = modBean.getModule(attachmentModuleName);
                }
                Map<String, List<Map<String, Object>>> subformMap = record.getSubForm();

                if(MapUtils.isNotEmpty(subformMap) && subformMap.containsKey(attachmentModuleName)){
                    List<Map<String, Object>> attachmentsAdded = subformMap.get(attachmentModuleName);

                    FacilioField parentIdField = modBean.getField("parentId", attachmentModule.getName());

                    if(CollectionUtils.isNotEmpty(attachmentsAdded)){
                        List<AttachmentV3Context> attachments = FieldUtil.getAsBeanListFromMapList(attachmentsAdded, AttachmentV3Context.class);
                        for(AttachmentV3Context v3Attachment : attachments){
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("parentId", v3Attachment.getParent().getId());
                            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                            updatedfields.add(parentIdField);
                            UpdateRecordBuilder<AttachmentContext> updateBuilder = new UpdateRecordBuilder<AttachmentContext>()
                                    .module(attachmentModule)
                                    .fields(updatedfields)
                                    .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", "1", CommonOperators.IS_EMPTY))
                                    .andCondition(CriteriaAPI.getCondition("PARENT", "parent", String.valueOf(v3Attachment.getParent().getId()), NumberOperators.EQUALS))

                                    ;
                            updateBuilder.updateViaMap(updateMap);
                        }
                    }
                }

            }
        }

        return false;
    }

    private String attachmentModuleName;

    public UpdateAttachmentsParentIdCommandV3(String attachmentModuleName) {
        this.attachmentModuleName = attachmentModuleName;
    }
    public UpdateAttachmentsParentIdCommandV3() {

    }
}
