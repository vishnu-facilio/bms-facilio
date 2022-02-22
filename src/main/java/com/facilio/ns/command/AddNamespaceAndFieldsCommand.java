package com.facilio.ns.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddNamespaceAndFieldsCommand extends FacilioCommand {
    Context ctx;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.ctx = context;
        addNamespace();
        addFields();
        return Boolean.FALSE;
    }

    private void addNamespace() throws Exception {
        NameSpaceContext ns = (NameSpaceContext) ctx.get(NSContextNames.NAMESPACE);
        Long parentRuleId = (Long) ctx.get(NSContextNames.PARENT_RULE_ID);

        if (parentRuleId != null) {
            ns.setParentRuleId(parentRuleId);
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields());
        long id = insertBuilder.insert(FieldUtil.getAsProperties(ns));
        ctx.put(NSContextNames.NAMESPACE_ID, id);
        ns.setId(id);

    }

    private void addFields() throws Exception {
        long nsId = (long) ctx.get(NSContextNames.NAMESPACE_ID);
        List<AssetContext> includedAssets = (List<AssetContext>) ctx.get(FacilioConstants.ContextNames.ASSETS);
        List<NameSpaceField> fields = (List<NameSpaceField>) ctx.get(NSContextNames.NAMESPACE_FIELDS);

        Objects.requireNonNull(fields);

        List<Map<String, Object>> assetList = new ArrayList<>();
        if (includedAssets != null) {
            for (AssetContext asset : includedAssets) {
                for (NameSpaceField fld : fields) {
                    prepareNSField(fld, nsId, asset.getId());
                    assetList.add(FieldUtil.getAsProperties(fld));
                }
            }
        } else {
            for (NameSpaceField fld : fields) {
                prepareNSField(fld, nsId, -1);
                assetList.add(FieldUtil.getAsProperties(fld));
            }
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFieldFields())
                .addRecords(assetList);

        insertBuilder.save();
    }

    private void prepareNSField(NameSpaceField fld, long nsId, long resourceId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField(fld.getField(), fld.getModule());
        Objects.requireNonNull(field, "Field cannot be null");
        fld.setFieldId(field.getFieldId());
        fld.setNsId(nsId);
        fld.setResourceId(resourceId);
    }
}
