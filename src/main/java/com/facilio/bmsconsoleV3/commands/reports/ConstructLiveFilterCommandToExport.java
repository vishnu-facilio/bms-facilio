package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFilterContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class ConstructLiveFilterCommandToExport extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        ReportContext reportContext = (ReportContext) context.get("report");
        ReadingAnalysisContext.ReportFilterMode criteriafilterMode = (ReadingAnalysisContext.ReportFilterMode) context.get(FacilioConstants.ContextNames.REPORT_FILTER_MODE);
        if(criteriafilterMode != null)
        {
            List<ReportFilterContext> filters = constructFilters(criteriafilterMode, (FacilioContext) context);
            if (filters != null) {
                reportContext.setFilters(filters);
            }
        }
        return false;
    }

    private List<ReportFilterContext> constructFilters (ReadingAnalysisContext.ReportFilterMode mode, FacilioContext context) throws Exception {

        if (mode != null) {
            Criteria criteria = null;
            ReportFilterContext filter = null;
            switch (mode) {
                case NONE:
                    return null;
                case ALL_ASSET_CATEGORY:
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    List<Long> categoryId = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
                    filter = getBasicReadingReportFilter(modBean, "parentId");
                    FacilioField categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
                    if(categoryId != null && categoryId.size() == 0)
                    {
                        return null;
                    }
                    criteria = new Criteria();
                    if(categoryId != null && categoryId.size()>0) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
                    }

                    filter.setCriteria(criteria);
                    return Collections.singletonList(filter);
                case SPECIFIC_ASSETS_OF_CATEGORY:
                    List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
                    if (parentIds == null || parentIds.isEmpty()) {
                        throw new IllegalArgumentException("Atleast one asset has to be mentioned for this xCriteri mode");
                    }

                    modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    filter = getBasicReadingReportFilter(modBean, "parentId");
                    filter.setDataFilter(true);
                    filter.setFilterOperator(PickListOperators.IS);
                    filter.setFilterValue(StringUtils.join(parentIds, ","));
                    return Collections.singletonList(filter);
                case SPACE:
                    modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    categoryId = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
                    List<Long> spaceId = (List<Long>) context.get(FacilioConstants.ContextNames.BASE_SPACE_LIST);
                    filter = getBasicReadingReportFilter(modBean, "parentId");
                    categoryField = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
                    FacilioField spaceField = modBean.getField("space", FacilioConstants.ContextNames.ASSET);

                    criteria = new Criteria();
                    if(categoryId != null && categoryId.size()>0) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(categoryField, categoryId, PickListOperators.IS));
                    }
                    if(spaceId != null) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(spaceField, spaceId, BuildingOperator.BUILDING_IS));
                    }

                    filter.setCriteria(criteria);
                    return Collections.singletonList(filter);
                case SITE:
                    return null;
            }
        }
        return null;
    }

    private ReportFilterContext getBasicReadingReportFilter(ModuleBean modBean, String filterFieldName) throws Exception {
        ReportFilterContext filter = new ReportFilterContext();
        filter.setFilterFieldName(filterFieldName);

        FacilioField field = modBean.getField("id", FacilioConstants.ContextNames.ASSET);
        filter.setField(field.getModule(), field);
        return filter;
    }
}
