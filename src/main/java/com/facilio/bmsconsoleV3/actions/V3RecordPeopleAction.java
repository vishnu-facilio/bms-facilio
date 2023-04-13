package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.actions.picklist.V3PIckListAction;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class V3RecordPeopleAction extends V3Action {
    String moduleName;
    Long recordId;
    public String getPeopleFromRecordFields() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getPeopleFromRecordFieldsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        chain.execute();
        setData("peopleFromRecordFields",context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }
}
