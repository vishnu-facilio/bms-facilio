package com.facilio.pdftemplate.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetTemplatesForDownloadFromPageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid moduleName");

        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        FacilioUtil.throwIllegalArgumentException(appId == null || appId <= 0, "Invalid appId to fetch pdfTemplates");

        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        FacilioUtil.throwIllegalArgumentException(recordId == null || recordId <= 0, "Invalid recordId to fetch pdfTemplates");

        List<PagesContext> customPages = CustomPageAPI.getAllCustomPage(appId, module.getModuleId(), PagesContext.PageLayoutType.WEB); //fetching all pages
        if(CollectionUtils.isNotEmpty(customPages)) {
            List<Long> customPageIds = new ArrayList<>();

            FacilioContext recordContext = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
            Map<String,Object> recordMap = (Map<String,Object>) recordContext.get("recordMap");
            boolean criteriaflag = false;
            for (PagesContext customPage : customPages) {
                    recordMap = FieldUtil.getAsProperties(CriteriaAPI.setLookupFieldsData(customPage.getCriteria(), recordMap));
                    criteriaflag = customPage.getCriteria() == null || customPage.getCriteria().computePredicate(recordMap).evaluate(((ArrayList<ModuleBaseWithCustomFields>) recordMap.get(moduleName)).get(0));
                    if (criteriaflag) {
                        customPageIds.add(customPage.getId());    //getting pageId that match the record's criteria
                    }
            }

            Condition pageIdCriteria = CriteriaAPI.getIdCondition(customPageIds, ModuleFactory.getPagesModule());

            Map<String, FacilioField> pageSectionWidgetField = FieldFactory.getAsMap(FieldFactory.getPageSectionWidgetsFields());
            Condition widgetTypeCondition = CriteriaAPI.getEqualsCondition(pageSectionWidgetField.get("widgetType"), PageWidget.WidgetType.PDF_VIEWER.name());
            FacilioField widgetIdField = pageSectionWidgetField.get("id");

            Criteria criteria = new Criteria();
            criteria.addAndCondition(pageIdCriteria);
            criteria.addAndCondition(widgetTypeCondition);
            List<Map<String, Object>> widgetIdsMapList = PagesUtil.getPageComponent(CustomPageAPI.PageComponent.WIDGET,Arrays.asList(widgetIdField), criteria);

            List<Long> pdfTemplateIds = null;
            if(CollectionUtils.isNotEmpty(widgetIdsMapList)) {

                List<Long> widgetIds = widgetIdsMapList.stream().map(f -> (Long) f.get("id")).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(widgetIds)) {

                    Map<String, FacilioField> templateWidgetRelationFieldMap = FieldFactory.getAsMap(FieldFactory.getPdfTemplateWidgetRelationFields());
                    GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                            .select(Arrays.asList((templateWidgetRelationFieldMap.get("templateId"))))
                            .table(ModuleFactory.getPdfTemplateWidgetRelation().getTableName())
                            .andCondition(CriteriaAPI.getEqualsCondition(templateWidgetRelationFieldMap.get("widgetId"), StringUtils.join(widgetIds, ",")));
                    List<Map<String, Object>> templateWidgetRelDetails = selectRecordBuilder.get();

                    if (CollectionUtils.isNotEmpty(templateWidgetRelDetails)) {
                        pdfTemplateIds = templateWidgetRelDetails.stream().map(f -> (Long) f.get("templateId")).collect(Collectors.toList());

                        if(CollectionUtils.isNotEmpty(pdfTemplateIds)) {
                            FacilioModule pdfTemplateModule = ModuleFactory.getPDFTemplatesModule();
                            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                                    .table(pdfTemplateModule.getTableName())
                                    .select(FieldFactory.getPDFTemplatesFields())
                                    .andCondition(CriteriaAPI.getModuleIdIdCondition(module.getModuleId(), ModuleFactory.getPDFTemplatesModule()))
                                    .andCondition(CriteriaAPI.getIdCondition(pdfTemplateIds, pdfTemplateModule));
                            List<Map<String, Object>> props = builder.get();

                            if (CollectionUtils.isNotEmpty(props)) {
                                List<PDFTemplate> pdfTemplates = FieldUtil.getAsBeanListFromMapList(props, PDFTemplate.class);
                                context.put(FacilioConstants.ContextNames.PDF_TEMPLATES, pdfTemplates);
                            }
                        }
                    }
                }
            }

        }
        return false;
    }
}
