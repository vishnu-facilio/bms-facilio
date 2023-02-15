package com.facilio.bmsconsole.imports;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.UpdateTimelineViewCalenderTypeCommand;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.annotations.AfterRowFunction;
import com.facilio.bmsconsole.imports.annotations.ImportModule;
import com.facilio.bmsconsole.imports.config.ImportConfig;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsoleV3.commands.failureclass.HandleFailureCauseAfterImportCommand;
import com.facilio.bmsconsoleV3.commands.failureclass.HandleFailureRemedyAfterImportCommand;
import com.facilio.bmsconsoleV3.commands.failureclass.WhitelistSystemFields;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.*;
import com.facilio.bmsconsoleV3.commands.pmImport.HandleResourcePlannerImportCommand;
import com.facilio.bmsconsoleV3.commands.pmImport.HandleTasksImportCommand;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.v3.context.Constants;

import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Supplier;

public class ImportConfiguration {

    @ImportModule(value = "test_module")
    public static Supplier<ImportConfig> getAssetImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .beforeUploadCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .parseHandler()
                .uniqueFunction((rowNumber, rowValue, context) -> {
                    return null;
                }).done()
                .importHandler()
                .beforeImportCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .build();
    }

    @ImportModule(value = "plannedmaintenance")
    public static Supplier<ImportConfig> getPlannedMaintenanceImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .beforeParseCommand(new WhitelistRequiredFieldsCommand())
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "pmPlanner")
    public static Supplier<ImportConfig> getPmPlannerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()
                .parseHandler()
                .done()
                .importHandler()
                .beforeImportCommand(new constructScheduleInfo() )
                .afterImportCommand(new fillTriggerIdAfterImport())
                .done()
                .build();
    }


    @ImportModule(value = "pmTrigger")
    public static Supplier<ImportConfig> getPmTriggerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "pmResourcePlanner")
    public static Supplier<ImportConfig> getPmResourcePlannerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .beforeImportCommand(new DeleteAndCreateResourcePlanner())
                .afterImportFunction(new AfterRowFunction() {

                    @Override
                    public Object apply(Integer rowNumber, Map<String, Object> rowValue, Map<String, Object> prop, Context context) throws Exception {
                        // TODO Auto-generated method stub
                        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
                        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

                        FacilioField plannerField = Constants.getModBean().getField("planner", FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);

                        String key = ImportAPI.getKeyNameFromField(plannerField);

                        String plannerString = (String) rowValue.get(fieldMapping.get(key));

                        Double pmId = (Double) prop.get("pmId");

                        if (plannerString != null && pmId != null) {

                            Criteria criteria = new Criteria();
                            criteria.addAndCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("name", FacilioConstants.PM_V2.PM_V2_PLANNER), plannerString, StringOperators.IS));
                            criteria.addAndCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("pmId", FacilioConstants.PM_V2.PM_V2_PLANNER), String.valueOf(pmId), NumberOperators.EQUALS));

                            SelectRecordsBuilder<PMPlanner> select = new SelectRecordsBuilder<PMPlanner>()
                                    .moduleName(FacilioConstants.PM_V2.PM_V2_PLANNER)
                                    .select(Constants.getModBean().getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER))
                                    .beanClass(PMPlanner.class)
                                    .andCriteria(criteria);

                            PMPlanner pmPlanner = select.fetchFirst();

                            if (pmPlanner != null) {
                                prop.put("planner", FieldUtil.getAsProperties(pmPlanner));
                            } else {
                                throw new ImportParseException(rowNumber, "PmPlanner", new Exception("PmPlanner does not belongs to current PM"));
                            }
                        }

                        return null;
                    }
                })
                .afterInsertCommand(new PmPlannerAfterInsertCommand())
                .done()
                .build();
    }

    @ImportModule(value = "resourceplanner")
    public static Supplier<ImportConfig> getPmImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .lookupMainFieldMap("plannedmaintenance", "name")
                .afterImportCommand(new HandleResourcePlannerImportCommand())
                .done()
                .build();
    }

    @ImportModule(value = "pmTasksImport")
    public static Supplier<ImportConfig> getPmTasksImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()
                .importHandler()
                .lookupMainFieldMap("plannedmaintenance", "name")
                .afterImportCommand(new HandleTasksImportCommand())
                .done()
                .build();
    }

    @ImportModule(value = "failureclass")
    public static Supplier<ImportConfig> getFailureClassConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()
                .parseHandler()
                .beforeParseCommand(new WhitelistSystemFields())
                .done()
                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "failurecode")
    public static Supplier<ImportConfig> getFailureCodeConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()
                .parseHandler()
                .beforeParseCommand(new WhitelistSystemFields())
                .done()
                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "failurecodecauses")
    public static Supplier<ImportConfig> getFailureCodeCausesConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()
                .parseHandler()
                .done()
                .importHandler()
                .afterImportCommand(new HandleFailureCauseAfterImportCommand())
                .done()
                .build();
    }

    @ImportModule(value = "failurecoderemedies")
    public static Supplier<ImportConfig> getFailureCodeRemediesConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()
                .parseHandler()
                .done()
                .importHandler()
                .afterImportCommand(new HandleFailureRemedyAfterImportCommand())
                .done()
                .build();

    }

    @ImportModule(value = FacilioConstants.CraftAndSKills.LABOUR_CRAFT)
    public static Supplier<ImportConfig> getLabourCraftAndSkillImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .importHandler()
                .afterImportFunction(new AfterRowFunction() {

                        @Override
                        public Object apply(Integer rowNumber, Map<String, Object> rowValue, Map<String, Object> prop, Context context) throws Exception {
                            // TODO Auto-generated method stub
                            ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
                            HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

                            FacilioField skillField = Constants.getModBean().getField("skill", FacilioConstants.CraftAndSKills.LABOUR_CRAFT);

                            String key = ImportAPI.getKeyNameFromField(skillField);

                            String skillString = (String) rowValue.get(fieldMapping.get(key));

                            Map<String, Object> craftMap = (Map<String, Object>) prop.get("craft");

                            if (skillString != null && craftMap != null) {

                                Criteria criteria = new Criteria();
                                criteria.addAndCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("name", FacilioConstants.CraftAndSKills.SKILLS), skillString, StringOperators.IS));
                                criteria.addAndCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("parentId", FacilioConstants.CraftAndSKills.SKILLS), craftMap.get("id") + "", NumberOperators.EQUALS));

                                SelectRecordsBuilder<SkillsContext> select = new SelectRecordsBuilder<SkillsContext>()
                                        .moduleName(FacilioConstants.CraftAndSKills.SKILLS)
                                        .select(Constants.getModBean().getAllFields(FacilioConstants.CraftAndSKills.SKILLS))
                                        .beanClass(SkillsContext.class)
                                        .andCriteria(criteria);

                                SkillsContext skill = select.fetchFirst();

                                if (skill != null) {
                                    prop.put("skill", FieldUtil.getAsProperties(skill));
                                } else {
                                    throw new ImportParseException(rowNumber, "Skill", new Exception("skill does not belongs to current craft"));
                                }
                            }

                            return null;
                        }
                    })
                .done()
                .build();
    }

}