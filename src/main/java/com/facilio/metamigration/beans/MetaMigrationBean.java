package com.facilio.metamigration.beans;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.db.criteria.Criteria;

import java.util.List;
import java.util.Map;

public interface MetaMigrationBean {
    public List<ApplicationContext> getAllApplications() throws Exception;

    public List<ApplicationLayoutContext> getLayoutsForAppId(long appId) throws Exception;

    public void addFormFields(long formId, FacilioForm form) throws Exception;

    public int deleteFormFields(List<Long> fieldIds) throws Exception;

    public List<Long> getFormRuleIds() throws Exception;

    public int deleteFormSections(List<Long> sectionIds) throws Exception;

    public List<Long> getModuleIdsWithForms() throws Exception;

    public void deleteForms(List<Long> formIds) throws Exception;

    public FacilioForm getForm(long formId) throws Exception;

    public FacilioForm getForm(String moduleName, String formName) throws Exception;

    public List<FacilioForm> getForms(Criteria criteria) throws Exception;

    public FormRuleContext getFormRule(long formRuleId) throws Exception;

    public long addFormRule(FormRuleContext formRuleContext) throws Exception;

    public void deleteFormRule(FormRuleContext formRuleContext) throws Exception;

    public long createForm(FacilioForm form, FacilioModule module) throws Exception;

    public Map<String, Object> createModules(List<FacilioModule> oldModules) throws Exception;

    public List<FacilioField> createFields(FacilioModule module, List<FacilioField> newFields) throws Exception;

    public WebTabContext getWebTabForAppAndRoute(long appId, String route) throws Exception;

    public WebtabWebgroupContext getWebTabWebGroupForTabId(long webTabId) throws Exception;

    public void addOrUpdateWebTabGroups(List<WebTabGroupContext> webTabGroups, Map<String, List<WebTabContext>> groupNameVsWebTabsMap, ApplicationLayoutContext appLayoutContext) throws Exception;
}
