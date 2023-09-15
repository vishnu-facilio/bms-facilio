package com.facilio.sandbox.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddOrUpdateSandboxCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        List<FacilioField> sandboxFields = FieldFactory.getFacilioSandboxFields();
        if(sandboxConfig.getId() <= 0) {
            Map<String, Object> sandboxProps = FieldUtil.getAsProperties(sandboxConfig);

            GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                    .table(sandboxModule.getTableName())
                    .fields(sandboxFields)
                    .addRecord(sandboxProps);

            insert.save();
            sandboxConfig.setId((long) sandboxProps.get("id"));
            sandboxConfig.setOrgId((long)sandboxProps.get("orgId"));
            context.put(SandboxConstants.SANDBOX_ID, sandboxConfig.getId());
        } else {
            SandboxAPI.updateSandboxConfig(sandboxConfig, FieldFactory.getFacilioSandboxUpdatableFields());
        }

        return false;
    }
}
