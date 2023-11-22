package com.facilio.fields.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.enums.Version;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.util.PermissionSetUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FieldConfigUtil {

    public static List<FacilioField> filterFieldsByPermission(@NonNull FacilioModule module, List<FacilioField> fields) throws Exception {
        return filterFieldsByPermission(module.getModuleId(), fields);
    }

    public static FacilioField filterFieldByPermission(@NonNull FacilioModule module, FacilioField field) throws Exception {
            return filterFieldByPermission(module.getModuleId(), field);
    }
    public static FacilioField filterFieldByPermission( long moduleId, FacilioField field) throws Exception {
        List<FacilioField> fields = filterFieldsByPermission(moduleId, new ArrayList<>(Arrays.asList(field)));
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0):null;
    }
    public static List<FacilioField> filterFieldsByPermission(long moduleId, List<FacilioField> fields) throws Exception {
        if (CollectionUtils.isNotEmpty(fields) && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FIELD_LIST_PERMISSION)) {
            fields = fields.stream()
                    .filter(f -> (f.getFieldId() <= 0) || PermissionSetUtil.getCurrentPeopleAccessibleFields(moduleId, f.getFieldId()))
                    .collect(Collectors.toList());
        }
        return fields;
    }

    public static Map<Long, FacilioModule> splitModules(FacilioModule module) {
        Map<Long, FacilioModule> modules = new HashMap<>();

        FacilioModule parent = module;
        while(parent != null) {
            modules.put(parent.getModuleId(), parent);
            parent = parent.getExtendModule();
        }
        return modules;
    }


    public static List<FacilioField> filterFieldsByCurrentVersion(@NonNull List<FacilioField> fields, boolean fetchUnModifiableList) {
        Version currentVersion = Version.getCurrentVersion();
        if(CollectionUtils.isNotEmpty(fields) && currentVersion != null) {
            long currentVersionId = currentVersion.getVersionId();
            fields.removeIf(f->f.getVersion() != null && ((f.getVersion() & currentVersionId) != currentVersionId));
        }
        if(fetchUnModifiableList) {
            return CollectionUtils.isNotEmpty(fields) ? Collections.unmodifiableList(fields) : null;
        }
        return fields;
    }
}
