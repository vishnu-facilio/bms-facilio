package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
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
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateTicketAttachmentsOldParentIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(CollectionUtils.isNotEmpty(wos)) {
            for(V3WorkOrderContext wo : wos){
                Map<String, List<Map<String, Object>>> subformMap = wo.getSubForm();

                if(MapUtils.isNotEmpty(subformMap) && subformMap.containsKey(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)){
                    List<Map<String, Object>> attachmentsAdded = subformMap.get(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

                    FacilioModule ticketAttachmentModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
                    FacilioField parentIdField = modBean.getField("parentId", ticketAttachmentModule.getName());

                    if(CollectionUtils.isNotEmpty(attachmentsAdded)){
                        List<AttachmentV3Context> attachments = FieldUtil.getAsBeanListFromMapList(attachmentsAdded, AttachmentV3Context.class);
                        for(AttachmentV3Context v3Attachment : attachments){
                            AttachmentContext att = new AttachmentContext();
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("parentId", v3Attachment.getParent().getId());
                            List<FacilioField> updatedfields = new ArrayList<FacilioField>();
                            updatedfields.add(parentIdField);
                            UpdateRecordBuilder<AttachmentContext> updateBuilder = new UpdateRecordBuilder<AttachmentContext>()
                                    .module(ticketAttachmentModule)
                                    .fields(updatedfields)
                                    .andCondition(CriteriaAPI.getCondition("PARENT_TICKET", "parentId", "1", CommonOperators.IS_EMPTY))
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
}
