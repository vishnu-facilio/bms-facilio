package com.facilio.modules.fields.relations;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.Collections;

public class MigrationUtil {

    public static void executeMigration() throws Exception {
        // Have migration commands for each org
        // Transaction is only org level. If failed, have to continue from the last failed org and not from first
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // chiller reading module migration
        FacilioModule readingModule = modBean.getModule("chillerreading");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = modBean.getField("runHours", readingModule.getName());
            if (runHoursField != null) {
                // few orgs doesn't have runHours field
                addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
            }
        }

        readingModule = modBean.getModule("ahureadinggeneral");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = modBean.getField("runHour", readingModule.getName());
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("fahureadinggeneral");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = modBean.getField("runHour", readingModule.getName());
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("coolingtowerreading");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = createRunHoursField(modBean, readingModule);
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("primaryPumpReadings");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = createRunHoursField(modBean, readingModule);
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("secondaryPumpReadings");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = createRunHoursField(modBean, readingModule);
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("condenserpumpreading");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = createRunHoursField(modBean, readingModule);
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

        readingModule = modBean.getModule("fcureadingdefault");
        if (readingModule != null) {
            FacilioField runStatusField = modBean.getField("runStatus", readingModule.getName());
            FacilioField runHoursField = createRunHoursField(modBean, readingModule);
            addTimeDeltaRelation(readingModule, runStatusField, runHoursField);
        }

//            LOGGER.info("Completed For -- "+ AccountUtil.getCurrentOrg().getId());
//            response.getWriter().println("Completed For -- "+AccountUtil.getCurrentOrg().getId());
    }

    private static FacilioField createRunHoursField(ModuleBean modBean, FacilioModule readingModule) throws Exception {
        FacilioField runHoursField = FieldFactory.getField("runHours", "Run Hours", "RUN_HOURS", readingModule, FieldType.DECIMAL);
        runHoursField.setDefault(true);
        runHoursField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        long l = modBean.addField(runHoursField);
        runHoursField.setFieldId(l);
        return runHoursField;
    }

    private static void addTimeDeltaRelation(FacilioModule readingModule, FacilioField runStatusField, FacilioField runHoursField) throws Exception {
        TimeDeltaRelationContext runStatusAndHoursRelation = new TimeDeltaRelationContext();
        runStatusAndHoursRelation.setBaseFieldModuleId(readingModule.getModuleId());
        runStatusAndHoursRelation.setBaseFieldId(runStatusField.getFieldId());
        runStatusAndHoursRelation.setDerivedFieldModuleId(readingModule.getModuleId());
        runStatusAndHoursRelation.setDerivedFieldId(runHoursField.getFieldId());
        runStatusAndHoursRelation.setType(2);
        runStatusAndHoursRelation.setUnit(1);
        RelationFieldUtil.addRelations(Collections.singletonList(runStatusAndHoursRelation));
    }
}