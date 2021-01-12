package com.facilio.modules.fields;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.modules.FacilioModule;

public class MultiEnumField extends BaseEnumField implements SupplementRecord {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String PARENT_FIELD_NAME = "parent";
    public static final String VALUE_FIELD_NAME = "index";

    public MultiEnumField() {
        // TODO Auto-generated constructor stub
        super();
    }

    protected MultiEnumField(MultiEnumField field) { // Do not forget to Handle here if new property is added
        super(field);
        this.relModuleId = field.relModuleId;
        this.relModule = field.relModule;
    }

    private long relModuleId = -1;
    public long getRelModuleId() {
        return relModuleId;
    }
    public void setRelModuleId(long relModuleId) {
        this.relModuleId = relModuleId;
    }

    private FacilioModule relModule;
    public FacilioModule getRelModule() {
        return relModule;
    }
    public void setRelModule(FacilioModule relModule) {
        this.relModule = relModule;
    }

    @Override
    public MultiEnumField clone() {
        // TODO Auto-generated method stub
        return new MultiEnumField(this);
    }

    @Override
    public FacilioField selectField() {
        return null;
    }

    @Override
    public FetchSupplementHandler newFetchHandler() {
        return new MultiEnumCRUDHandler(this);
    }

    @Override
    public InsertSupplementHandler newInsertHandler() {
        return new MultiEnumCRUDHandler(this);
    }

    @Override
    public UpdateSupplementHandler newUpdateHandler() {
        return new MultiEnumCRUDHandler(this);
    }

    @Override
    public DeleteSupplementHandler newDeleteHandler() {
        return new MultiEnumCRUDHandler(this);
    }
    
    public String getValue(List<Integer> indexes) {
    		if (getValues() != null && CollectionUtils.isNotEmpty(indexes)) {
    			StringBuilder builder = new StringBuilder();
				for(int i = 0, size = indexes.size();i < size; i++) {
					int indexValue = indexes.get(i);
					builder.append(getValue((int) indexValue));
					if (i+1 != size) {
						builder.append(", ");
					}
				}
				return builder.toString();
    		}
    		return null;
    }
}
