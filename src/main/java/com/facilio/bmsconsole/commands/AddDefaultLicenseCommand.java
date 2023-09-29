package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.util.DBConf;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class AddDefaultLicenseCommand extends FacilioCommand {
    private static final Set<AccountUtil.FeatureLicense> DEFAULT_FEATURE_LIST = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            AccountUtil.FeatureLicense.MAINTENANCE,
            AccountUtil.FeatureLicense.ALARMS,
            AccountUtil.FeatureLicense.ENERGY,
            AccountUtil.FeatureLicense.SPACE_ASSET,
            AccountUtil.FeatureLicense.NEW_LAYOUT,
            AccountUtil.FeatureLicense.APPROVAL,
            AccountUtil.FeatureLicense.SCHEDULED_WO,
            AccountUtil.FeatureLicense.NEW_ALARMS,
            AccountUtil.FeatureLicense.SKIP_TRIGGERS,
            AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION,
            AccountUtil.FeatureLicense.NEW_APPROVALS,
            AccountUtil.FeatureLicense.SCOPING,
            AccountUtil.FeatureLicense.CUSTOM_MAIL,
            AccountUtil.FeatureLicense.MULTISITEPM,
            AccountUtil.FeatureLicense.NEW_READING_RULE,
            AccountUtil.FeatureLicense.SCRIPT_CRUD_FROM_V3,
            AccountUtil.FeatureLicense.RESOURCES,
            AccountUtil.FeatureLicense.SCOPE_VARIABLE,
            AccountUtil.FeatureLicense.EMAIL_TRACKING,
            AccountUtil.FeatureLicense.WORKFLOW_LOG,
            AccountUtil.FeatureLicense.RESOURCE_SCHEDULER,
            AccountUtil.FeatureLicense.NEW_V3API,
            AccountUtil.FeatureLicense.NEW_SITE_SUMMARY,
            AccountUtil.FeatureLicense.WEATHER_INTEGRATION,
            AccountUtil.FeatureLicense.MAINTENANCE_APP_SIGNUP,
            AccountUtil.FeatureLicense.NEW_SETUP,
            AccountUtil.FeatureLicense.WO_STATE_TRANSITION_V3,
            AccountUtil.FeatureLicense.PM_PLANNER,
            AccountUtil.FeatureLicense.SCOPE_SUBQUERY,
            AccountUtil.FeatureLicense.NEW_DASHBOARD_FLOW,
            AccountUtil.FeatureLicense.PEOPLE_USER_SCOPING,
            AccountUtil.FeatureLicense.SHIFT,
            AccountUtil.FeatureLicense.ATTENDANCE,
            AccountUtil.FeatureLicense.PERMISSION_SET,
            AccountUtil.FeatureLicense.IMPORT_DATA,
            AccountUtil.FeatureLicense.FIELD_LIST_PERMISSION,
            AccountUtil.FeatureLicense.PLANNED_INVENTORY,
            AccountUtil.FeatureLicense.NEW_SCHEDULED_WORKFLOW_RULE,
            AccountUtil.FeatureLicense.SENSOR_RULE,
            AccountUtil.FeatureLicense.METER
            // Add new default licenses here
    )));

    private String licenseMappingKey (AccountUtil.FeatureLicense license) {
        return license.getGroup().getLicenseKey();
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        

        Map<String, Object> defaultLicenseVal = DEFAULT_FEATURE_LIST.stream()
                .collect(Collectors.groupingBy(
                                this::licenseMappingKey,
                                Collectors.collectingAndThen(Collectors.summingLong(
                                        AccountUtil.FeatureLicense::getLicense
                                ), Object.class::cast)
                        )
                );

        Map<String,Object> preProdInsertValue = new HashMap<>(defaultLicenseVal);
        new GenericInsertRecordBuilder()
                .table("FeatureLicense")
                .fields(AccountConstants.getFeatureLicenseFields(true))
                .insert(defaultLicenseVal);

        GenericInsertRecordBuilder preProdInsert = new GenericInsertRecordBuilder()
                .table(AccountConstants.getPreProdFeatureLicenseModule().getTableName())
                .fields(AccountConstants.getPreProdFeatureLicenseFields());
        preProdInsert.insert(preProdInsertValue);

        LOGGER.info("Default license for org => " + DBConf.getInstance().getCurrentOrgId() + " is "+defaultLicenseVal);

        return false;
    }

    public static void main(String[] args) {
        Map<AccountUtil.LicenseMapping, Long> defaultLicense = new HashMap<>();
        defaultLicense.put(AccountUtil.LicenseMapping.GROUP1LICENSE, 2269735674725455l);
        defaultLicense.put(AccountUtil.LicenseMapping.GROUP2LICENSE, 138518233608l);

        for (AccountUtil.FeatureLicense license : AccountUtil.FeatureLicense.values()) {
            if (license.isEnabled(defaultLicense)) {
                System.out.println(license);
            }
        }
    }
}
