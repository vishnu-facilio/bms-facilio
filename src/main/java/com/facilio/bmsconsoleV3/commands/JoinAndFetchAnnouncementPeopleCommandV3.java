package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.JoinContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinAndFetchAnnouncementPeopleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean fetchAnnouncementPeople = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "fetchAnnouncementPeople"));
        if(fetchAnnouncementPeople) {
            Long announcementId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "announcementId"));
            String moduleName = Constants.getModuleName(context);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule peopleAnnouncementModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(peopleAnnouncementModule.getName()));
            List<JoinContext> joinContextList = new ArrayList<>();
            joinContextList.add(new JoinContext(
                    peopleAnnouncementModule,
                    fieldsMap.get("people"),
                    FieldFactory.getIdField(modBean.getModule(moduleName)),
                    JoinContext.JoinType.INNER_JOIN
            ));
            context.put(Constants.JOINS, joinContextList);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId").getCompleteColumnName(), "parentId", String.valueOf(announcementId), NumberOperators.EQUALS));
            boolean isRead = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "isRead"));
            if(isRead){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("isRead").getCompleteColumnName(), "isRead", String.valueOf(true), BooleanOperators.IS));
            }
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        }
        return false;
    }
}
