package com.facilio.telemetry.beans;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.telemetry.context.TelemetryCriteriaCacheContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.telemetry.signup.AddTelemetryCriteriaModule;
import com.facilio.telemetry.util.TelemetryCriteriaAPI;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.ns.NamespaceAPI.constructNamespaceAndFields;

public class TelemetryCriteriaBeanImpl implements TelemetryCriteriaBean {
    @Override
    public TelemetryCriteriaCacheContext fetchTelemetryCriteria(long telemetryCriteriaId) throws Exception {
        TelemetryCriteriaContext telemetryCriteria = V3RecordAPI.getRecord(AddTelemetryCriteriaModule.MODULE_NAME, telemetryCriteriaId, TelemetryCriteriaContext.class);
        if(telemetryCriteria != null && telemetryCriteria.getNamespaceId() != null) {
            telemetryCriteria.setNamespace(getNamespace(telemetryCriteria.getNamespaceId()));
        }
        return new TelemetryCriteriaCacheContext(telemetryCriteria);
    }

    public NameSpaceContext getNamespace(Long nsId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .leftJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldRelatedModule().getTableName()).on("Namespace_Fields.ID=Namespace_Field_Related.NAMESPACE_FIELD_ID")
                .andCondition(CriteriaAPI.getIdCondition(nsId, NamespaceModuleAndFieldFactory.getNamespaceModule()));

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<NameSpaceContext> nameSpaceContexts = constructNamespaceAndFields(props);
            return nameSpaceContexts.get(0);
        }
        throw new Exception("Invalid namespace Id");

    }
}