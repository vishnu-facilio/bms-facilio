package com.facilio.componentpackage.implementation;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.iam.accounts.bean.IAMOrgBean;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.time.TimeFormat;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgInfoPackageBeanImpl implements PackageBean<Organization> {
    // TODO - Handle LogoId
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Map<Long, Long> resultProp = new HashMap<>();
        resultProp.put(currentOrg.getOrgId(), -1L);
        return resultProp;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Organization> fetchComponents(List<Long> ids) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Map<Long, Organization> resultProp = new HashMap<>();
        resultProp.put(currentOrg.getOrgId(), currentOrg);
        return resultProp;
    }

    @Override
    public void convertToXMLComponent(Organization organization, XMLBuilder element) throws Exception {
        element.element(PackageConstants.OrgConstants.ORGNAME).text(organization.getName());
        element.element(PackageConstants.OrgConstants.PHONE).text(organization.getPhone());
        element.element(PackageConstants.OrgConstants.MOBILE).text(organization.getMobile());
        element.element(PackageConstants.OrgConstants.FAX).text(organization.getFax());
        element.element(PackageConstants.OrgConstants.STREET).text(organization.getStreet());
        element.element(PackageConstants.OrgConstants.CITY).text(organization.getCity());
        element.element(PackageConstants.OrgConstants.STATE).text(organization.getState());
        element.element(PackageConstants.OrgConstants.ZIP).text(organization.getZip());
        element.element(PackageConstants.OrgConstants.COUNTRY).text(organization.getCountry());
        element.element(PackageConstants.OrgConstants.CURRENCY).text(organization.getCurrency());
        element.element(PackageConstants.OrgConstants.TIMEZONE).text(organization.getTimezone());
        element.element(PackageConstants.OrgConstants.DATE_FORMAT).text(organization.getDateFormat());
        element.element(PackageConstants.OrgConstants.LANGUAGE).text(organization.getLanguage());
        element.element(PackageConstants.OrgConstants.FACILIODOMAINNAME).text(organization.getDomain());
        element.element(PackageConstants.OrgConstants.TIME_FORMAT)
                .text(organization.getTimeFormatEnum() != null ? organization.getTimeFormatEnum().name() : null);
        element.element(PackageConstants.OrgConstants.BUSINESS_HOUR).text(String.valueOf(organization.getBusinessHour()));
        element.element(PackageConstants.OrgConstants.ALLOW_USER_TIMEZONE)
                .text(organization.getAllowUserTimeZone() != null ? String.valueOf(organization.getAllowUserTimeZone()) : Boolean.FALSE.toString());

        Map<String, Long> featureLicenseMap = AccountUtil.getOrgBean().getFeatureLicense();
        if (MapUtils.isNotEmpty(featureLicenseMap)) {
            XMLBuilder featureLicenseList = element.element(PackageConstants.OrgConstants.FEATURE_LICENSE_LIST);
            for (String licenseKey : featureLicenseMap.keySet()) {
                XMLBuilder featureLicenseElement = featureLicenseList.element(PackageConstants.OrgConstants.FEATURE_LICENSE);
                featureLicenseElement.element(PackageConstants.OrgConstants.FEATURE_LICENSE_KEY).text(licenseKey);
                featureLicenseElement.element(PackageConstants.OrgConstants.FEATURE_LICENSE_VALUE).text(String.valueOf(featureLicenseMap.get(licenseKey)));
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Map<String, Long> resultProp = new HashMap<>();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            resultProp.put(uniqueIdentifier, currentOrg.getOrgId());
        }
        return resultProp;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return new HashMap<>();
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        IAMOrgBean orgBean = IAMUtil.getOrgBean();
        for (Map.Entry<Long, XMLBuilder> uniqueIdentifierVsComponent : idVsXMLComponents.entrySet()) {
            Long orgId = uniqueIdentifierVsComponent.getKey();
            XMLBuilder element = uniqueIdentifierVsComponent.getValue();
            Organization organization = constructOrgFromBuilder(element);

            // Update Org
            orgBean.updateOrgv2(orgId, organization);

            // Update FeatureLicense
            XMLBuilder featureLicenseList = element.getElement(PackageConstants.OrgConstants.FEATURE_LICENSE_LIST);
            if (featureLicenseList != null) {
                Map<String, Long> licenseMapFromBuilder = new HashMap<>();
                List<XMLBuilder> featureLicenseElements = featureLicenseList.getFirstLevelElementListForTagName(PackageConstants.OrgConstants.FEATURE_LICENSE);
                for (XMLBuilder featureLicense : featureLicenseElements) {
                    String licenseKey = featureLicense.getElement(PackageConstants.OrgConstants.FEATURE_LICENSE_KEY).getText();
                    long license = Long.parseLong(featureLicense.getElement(PackageConstants.OrgConstants.FEATURE_LICENSE_VALUE).getText());
                    licenseMapFromBuilder.put(licenseKey, license);
                }

                Map<AccountUtil.LicenseMapping, Long> licenseMap = new HashMap<>();
                for (AccountUtil.LicenseMapping licenseMapping : AccountUtil.LicenseMapping.values()) {
                    if (licenseMapFromBuilder.containsKey(licenseMapping.getLicenseKey())) {
                        licenseMap.put(licenseMapping, licenseMapFromBuilder.get(licenseMapping.getLicenseKey()));
                    }
                }

                AccountUtil.getTransactionalOrgBean(AccountUtil.getCurrentOrg().getOrgId()).addLicence(licenseMap);
            }
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    private Organization constructOrgFromBuilder(XMLBuilder element) {
        String orgName = element.getElement(PackageConstants.OrgConstants.ORGNAME).getText();
        String phone = element.getElement(PackageConstants.OrgConstants.PHONE).getText();
        String mobile = element.getElement(PackageConstants.OrgConstants.MOBILE).getText();
        String fax = element.getElement(PackageConstants.OrgConstants.FAX).getText();
        String street = element.getElement(PackageConstants.OrgConstants.STREET).getText();
        String city = element.getElement(PackageConstants.OrgConstants.CITY).getText();
        String state = element.getElement(PackageConstants.OrgConstants.STATE).getText();
        String zip = element.getElement(PackageConstants.OrgConstants.ZIP).getText();
        String country = element.getElement(PackageConstants.OrgConstants.COUNTRY).getText();
        String currency = element.getElement(PackageConstants.OrgConstants.CURRENCY).getText();
        String timeZone = element.getElement(PackageConstants.OrgConstants.TIMEZONE).getText();
        String dateFormat = element.getElement(PackageConstants.OrgConstants.DATE_FORMAT).getText();
        String language = element.getElement(PackageConstants.OrgConstants.LANGUAGE).getText();
        String facilioDomainName = element.getElement(PackageConstants.OrgConstants.FACILIODOMAINNAME).getText();
        String timeFormatStr = element.getElement(PackageConstants.OrgConstants.TIME_FORMAT).getText();
        long businessHour = Long.parseLong(element.getElement(PackageConstants.OrgConstants.BUSINESS_HOUR).getText());
        boolean allowUserTimeZone = Boolean.parseBoolean(element.getElement(PackageConstants.OrgConstants.ALLOW_USER_TIMEZONE).getText());

        Organization organization = new Organization();
        organization.setName(orgName);
        organization.setPhone(phone);
        organization.setMobile(mobile);
        organization.setFax(fax);
        organization.setStreet(street);
        organization.setCity(city);
        organization.setState(state);
        organization.setZip(zip);
        organization.setCountry(country);
        organization.setCurrency(currency);
        organization.setTimezone(timeZone);
        organization.setDateFormat(dateFormat);
        organization.setLanguage(language);
        organization.setDomain(facilioDomainName);
        organization.setBusinessHour(businessHour);
        organization.setAllowUserTimeZone(allowUserTimeZone);
        if (StringUtils.isNotEmpty(timeFormatStr)) {
            organization.setTimeFormat(TimeFormat.valueOf(timeFormatStr).getIndex());
        }

        return organization;
    }
}
