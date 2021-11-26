package com.facilio.modules.fields;

import com.facilio.db.builder.DBUtil;
import com.facilio.util.FacilioStreamUtil;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SupplementRecord {
    public String linkName(); // Unique name used to avoid error when same supplement record is added twice or something

    public FacilioField selectField();

    public FetchSupplementHandler newFetchHandler();

    public InsertSupplementHandler newInsertHandler();

    public UpdateSupplementHandler newUpdateHandler();

    public DeleteSupplementHandler newDeleteHandler();

    public static Predicate<SupplementRecord> distinctSupplementRecord() {
        return FacilioStreamUtil.distinctByKey(SupplementRecord::linkName);
    }

    public static Collection<SupplementRecord> filterSystemSupplement(Collection<FacilioField> fields) {
        return Optional.ofNullable(fields).map(Collection::stream).orElseGet(Stream::empty)
                .filter(DBUtil::isSystemSupplement)
                .map(SupplementRecord.class::cast)
                .collect(Collectors.toList());
    }
}
