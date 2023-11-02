package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.command.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchQuestionsFromPagesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        Long responseId = getResponseId((FacilioContext) context);
        String responseModuleName = getResponseModuleName((FacilioContext) context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long createdTime=null;
        if(responseId!=null && responseModuleName!=null) {
            FacilioModule module = modBean.getModule(responseModuleName);
            FacilioField field = modBean.getField("createdTime",responseModuleName);
            if(field!=null) {
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(Collections.singletonList(field))
                        .table(modBean.getModule(responseModuleName).getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(responseId, module));

                Map<String, Object> responseList = selectBuilder.fetchFirst();
                createdTime = FacilioUtil.parseLong(responseList.get("createdTime"));
            }
        }
        context.put("responseCreatedTime",createdTime);
//        if(createdTime!=null){
//            FacilioField questionSysCreatedTimeField = modBean.getField("sysCreatedTime", FacilioConstants.QAndA.QUESTION);
//            Criteria criteria = new Criteria();
//            Condition condition = CriteriaAPI.getCondition(questionSysCreatedTimeField, String.valueOf(createdTime), DateOperators.IS_BEFORE);
//            criteria.addAndCondition(condition);
//            QAndAUtil.populateQuestionsInPages(pages,criteria);
//        } else {
            QAndAUtil.populateQuestionsInPages(pages);
//        }
        return false;
    }
    private Long getResponseId (FacilioContext context) {
        Object responseId = Constants.getQueryParam(context, "response");
        return responseId == null ? null : FacilioUtil.parseLong(responseId);
    }
    private String getResponseModuleName (FacilioContext context) {
        Object responseModuleName = Constants.getQueryParam(context, "responseModuleName");
        return responseModuleName == null ? null : (String) responseModuleName;
    }
}
