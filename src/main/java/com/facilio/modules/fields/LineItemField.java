package com.facilio.modules.fields;

import com.facilio.modules.FacilioModule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter @Setter
public class LineItemField extends FacilioField implements SupplementRecord {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LineItemField() {
        super();
    }

    public LineItemField (LineItemField field) {
        super(field);
        this.childModuleId = field.childModuleId;
        this.childModule = field.childModule;
        this.childLookupFieldId = field.childLookupFieldId;
        this.childLookupField = field.childLookupField;
    }

    @Override
    public LineItemField clone() {
        return new LineItemField(this);
    }

    private long childModuleId = -1, childLookupFieldId;
    private FacilioModule childModule;
    private LookupField childLookupField;

    @Override
    public FacilioField selectField() {
        return null;
    }

    @Override
    public FetchSupplementHandler newFetchHandler() {
        return new LineItemCRUDHandler(this);
    }

    @Override
    public InsertSupplementHandler newInsertHandler() {
        return new LineItemCRUDHandler(this);
    }

    @Override
    public UpdateSupplementHandler newUpdateHandler() {
        return new LineItemCRUDHandler(this);
    }

    @Override
    public DeleteSupplementHandler newDeleteHandler() {
        return new LineItemCRUDHandler(this);
    }
}
