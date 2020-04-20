<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.FieldType" %>

<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.BooleanField" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.modules.fields.*" %>
<%@ page import="com.facilio.tasker.job.JobStore" %>
<%@ page import="com.facilio.tasker.job.JobContext" %>
<%@ page import="com.facilio.accounts.dto.Account" %>


<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
            if (assetModule == null) {
                return false;
            }

            FacilioField salvageAmount = FieldFactory.getField("salvageAmount", "Salvage Amount", "SALVAGE_AMOUNT", assetModule, FieldType.NUMBER);
            salvageAmount.setDefault(true);
            salvageAmount.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(salvageAmount);

            FacilioField currentPriceField = FieldFactory.getField("currentPrice", "Current Price", "CURRENT_PRICE", assetModule, FieldType.NUMBER);
            currentPriceField.setDefault(true);
            currentPriceField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(currentPriceField);


            FacilioModule module = new FacilioModule();
            module.setName("assetdepreciation");
            module.setDisplayName("Asset Depreciation");
            module.setTableName("AssetDepreciation");
            module.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            module.setType(FacilioModule.ModuleType.BASE_ENTITY);
            long modId = modBean.addModule(module);
            module.setModuleId(modId);

            FacilioField name = FieldFactory.getField("name","Name","NAME",module,FieldType.STRING);
            name.setDisplayType(1);
            name.setDefault(true);
            name.setRequired(true);
            name.setMainField(true);

            com.facilio.modules.fields.SystemEnumField type = (SystemEnumField) FieldFactory.getField("depreciationType","Depreciation Type","DEPRECIATION_TYPE",module,FieldType.SYSTEM_ENUM);
            type.setEnumName("DepreciationType");
            type.setDisplayType(3);
            type.setDefault(true);

            NumberField assetId = new NumberField(module, "assetId", "Asset ID", FacilioField.FieldDisplayType.NUMBER, "ASSET_ID", FieldType.NUMBER, true, false, true, true);

            NumberField freq = new NumberField(module, "frequency", "Frequency", FacilioField.FieldDisplayType.NUMBER, "FREQUENCY", FieldType.NUMBER, true, false, true, true);

            SystemEnumField freqType = (SystemEnumField) FieldFactory.getField("frequencyType","'Frequency Type","FREQUENCY_TYPE",module,FieldType.SYSTEM_ENUM);
            freqType.setEnumName("FrequencyType");
            freqType.setDisplayType(3);
            freqType.setDefault(true);

            FacilioField startDate = FieldFactory.getField("startDate","Start Date","START_DATE",module,FieldType.DATE);
            startDate.setDisplayType(6);
            startDate.setDefault(true);
            startDate.setRequired(true);
            startDate.setMainField(true);

            FacilioField isActive = FieldFactory.getField("active","Active","ACTIVE",module,FieldType.BOOLEAN);
            isActive.setDisplayType(5);
            isActive.setDefault(true);

            modBean.addField(name);
            modBean.addField(type);
            modBean.addField(assetId);
            modBean.addField(freq);
            modBean.addField(freqType);
            modBean.addField(startDate);
            modBean.addField(isActive);


            FacilioModule depreciationCalculationModule = new FacilioModule();
            depreciationCalculationModule.setName("assetdepreciationCalculation");
            depreciationCalculationModule.setDisplayName("Asset Depreciation Calculation");
            depreciationCalculationModule.setTableName("Asset_Depreciation_Calculation");
            depreciationCalculationModule.setType(FacilioModule.ModuleType.SUB_ENTITY);
            depreciationCalculationModule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            long depreciationCalculationModuleId = modBean.addModule(depreciationCalculationModule);
            depreciationCalculationModule.setModuleId(depreciationCalculationModuleId);

            LookupField lookupField = (LookupField) FieldFactory.getField("asset", "Asset", "ASSET_ID", depreciationCalculationModule, FieldType.LOOKUP);
            lookupField.setDefault(true);
            lookupField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
            lookupField.setLookupModule(assetModule);
            modBean.addField(lookupField);

            FacilioField currentPrice = FieldFactory.getField("currentPrice", "Current Price", "CURRENT_PRICE", depreciationCalculationModule, FieldType.NUMBER);
            currentPrice.setDefault(true);
            currentPrice.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(currentPrice);

            FacilioField calculatedField = FieldFactory.getField("calculatedDate", "Calculated Date", "CALCULATED_DATE", depreciationCalculationModule, FieldType.NUMBER);
            calculatedField.setDefault(true);
            calculatedField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(calculatedField);

            FacilioField depreciationAmount = FieldFactory.getField("depreciatedAmount", "Depreciated Amount", "DEPRECIATED_AMOUNT", depreciationCalculationModule, FieldType.NUMBER);
            depreciationAmount.setDefault(true);
            depreciationAmount.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(depreciationAmount);

            FacilioField depreciationField = FieldFactory.getField("depreciationId", "Depreciation Id", "DEPRECIATION_ID", depreciationCalculationModule, FieldType.NUMBER);
            depreciationField.setDefault(true);
            depreciationField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            modBean.addField(depreciationField);

            JobContext job = new JobContext();
            job.setJobId(AccountUtil.getCurrentOrg().getOrgId());
            job.setJobName("AssetDepreciationJob");
            job.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            job.setActive(true);
            job.setTransactionTimeout(7200000);
            job.setIsPeriodic(true);
            job.setScheduleJson("{\"times\":[\"00:00\"],\"frequencyType\":1,\"frequencyTypeEnum\":\"DAILY\"}");
            job.setExecutorName("facilio");
            JobStore.addJob(job);

            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>