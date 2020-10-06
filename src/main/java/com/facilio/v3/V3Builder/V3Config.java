package com.facilio.v3.V3Builder;

import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldsCount;
import org.apache.commons.chain.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3Config implements V3Builder {
    private String moduleName;
    private Class beanClass;
    private CreateHandler createHandler;
    private UpdateHandler updateHandler;
    private DeleteHandler deleteHandler;
    private ListHandler listHandler;
    private SummaryHandler summaryHandler;
    private ModuleCustomFieldsCount customFieldsCount;

    public SummaryHandler getSummaryHandler() {
        return summaryHandler;
    }

    public void setSummaryHandler(SummaryHandler summaryHandler) {
        this.summaryHandler = summaryHandler;
    }

    public ListHandler getListHandler() {
        return listHandler;
    }

    public void setListHandler(ListHandler listHandler) {
        this.listHandler = listHandler;
    }

    public DeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(DeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public CreateHandler getCreateHandler() {
        return createHandler;
    }

    public void setCreateHandler(CreateHandler createHandler) {
        this.createHandler = createHandler;
    }

    public V3Config(Class bean, ModuleCustomFieldsCount customFieldsCount) {
        this.beanClass = bean;
        this.customFieldsCount = customFieldsCount;
    }

    public UpdateHandler update() {
        if (this.updateHandler == null) {
            this.updateHandler = new UpdateHandler(this);
        }
        return this.updateHandler;
    }

    public CreateHandler create() {
        if (this.createHandler == null) {
            this.createHandler = new CreateHandler(this);
        }
        return this.createHandler;
    }

    public DeleteHandler delete() {
        if (this.deleteHandler == null) {
            this.deleteHandler = new DeleteHandler(this);
        }
        return this.deleteHandler;
    }

    public SummaryHandler summary() {
        if (this.summaryHandler == null) {
            this.summaryHandler = new SummaryHandler(this);
        }
        return this.summaryHandler;
    }

    public ListHandler list() {
        if (this.listHandler == null) {
            this.listHandler = new ListHandler(this);
        }
        return this.listHandler;
    }

    public V3Config build() {
        return this;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public ModuleCustomFieldsCount getCustomFieldsCount() {
        return customFieldsCount;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public class CreateHandler implements CreateBuilder {
        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;
        private V3Builder parent;

        private CreateHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public CreateHandler init(Command initCommand) {
            this.initCommand = initCommand;
            return this;
        }

        @Override
        public CreateHandler beforeSave(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
            return this;
        }

        @Override
        public CreateHandler afterSave(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
            return this;
        }

        @Override
        public CreateBuilder afterTransaction(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeSaveCommand() {
            return beforeSaveCommand;
        }

        public void setBeforeSaveCommand(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
        }

        public Command getAfterSaveCommand() {
            return afterSaveCommand;
        }

        public void setAfterSaveCommand(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }
    }

    public class UpdateHandler implements UpdateBuilder {
        private Command initCommand;
        private Command beforeSaveCommand;
        private Command afterSaveCommand;
        private Command afterTransactionCommand;

        private V3Builder parent;

        private UpdateHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public UpdateHandler init(Command initCommand) {
            this.initCommand = initCommand;
            return this;
        }

        @Override
        public UpdateHandler beforeSave(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
            return this;
        }

        @Override
        public UpdateHandler afterSave(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
            return this;
        }

        @Override
        public UpdateBuilder afterTransaction(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
            return this;
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }


        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeSaveCommand() {
            return beforeSaveCommand;
        }

        public void setBeforeSaveCommand(Command beforeSaveCommand) {
            this.beforeSaveCommand = beforeSaveCommand;
        }

        public Command getAfterSaveCommand() {
            return afterSaveCommand;
        }

        public void setAfterSaveCommand(Command afterSaveCommand) {
            this.afterSaveCommand = afterSaveCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }
    }

    public class DeleteHandler implements DeleteBuilder {
        private Command initCommand;
        private Command beforeDeleteCommand;
        private Command afterDeleteCommand;
        private Command afterTransactionCommand;

        private V3Builder parent;

        private DeleteHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public DeleteHandler init(Command initCommand) {
            this.initCommand = initCommand;
            return this;
        }

        @Override
        public DeleteHandler beforeDelete(Command beforeDeleteCommand) {
            this.beforeDeleteCommand = beforeDeleteCommand;
            return this;
        }

        @Override
        public DeleteHandler afterDelete(Command afterDeleteCommand) {
            this.afterDeleteCommand = afterDeleteCommand;
            return this;
        }

        @Override
        public DeleteBuilder afterTransaction(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getInitCommand() {
            return initCommand;
        }

        public void setInitCommand(Command initCommand) {
            this.initCommand = initCommand;
        }

        public Command getBeforeDeleteCommand() {
            return beforeDeleteCommand;
        }

        public void setBeforeDeleteCommand(Command beforeDeleteCommand) {
            this.beforeDeleteCommand = beforeDeleteCommand;
        }

        public Command getAfterDeleteCommand() {
            return afterDeleteCommand;
        }

        public void setAfterDeleteCommand(Command afterDeleteCommand) {
            this.afterDeleteCommand = afterDeleteCommand;
        }

        public Command getAfterTransactionCommand() {
            return afterTransactionCommand;
        }

        public void setAfterTransactionCommand(Command afterTransactionCommand) {
            this.afterTransactionCommand = afterTransactionCommand;
        }
    }



    public class ListHandler implements ListBuilder {
        private Command beforeFetchCommand;
        private Command afterFetchCommand;
        private Command beforeCountCommand;
        private V3Builder parent;
        private boolean showStateFlowList;
        private Map<String, List<String>> lookupFieldCriteriaMap;

        private ListHandler(V3Builder parent) {
            this.parent = parent;
            this.lookupFieldCriteriaMap = new HashMap<>();
        }

        @Override
        public ListBuilder beforeFetch(Command criteriaCommand) {
            this.beforeFetchCommand = criteriaCommand;
            return this;
        }

        @Override
        public ListHandler afterFetch(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
            return this;
        }

        @Override
        public ListBuilder showStateFlowList() {
            this.showStateFlowList = true;
            return this;
        }

        @Override
        public ListBuilder fetchRelations(String moduleName, String lookupField) {
            if (this.lookupFieldCriteriaMap.get(moduleName) == null) {
                this.lookupFieldCriteriaMap.put(moduleName, new ArrayList<>());
            }
            this.lookupFieldCriteriaMap.get(moduleName).add(lookupField);
            return this;
        }

        @Override
        public ListBuilder beforeCount(Command beforeCountCommand) {
            this.beforeCountCommand = beforeCountCommand;
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public SummaryBuilder summary() {
            return this.parent.summary();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getBeforeFetchCommand() {
            return beforeFetchCommand;
        }

        public void setBeforeFetchCommand(Command beforeFetchCommand) {
            this.beforeFetchCommand = beforeFetchCommand;
        }

        public Command getAfterFetchCommand() {
            return afterFetchCommand;
        }

        public void setAfterFetchCommand(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
        }

        public boolean isShowStateFlowList() {
            return showStateFlowList;
        }

        public void setShowStateFlowList(boolean showStateFlowList) {
            this.showStateFlowList = showStateFlowList;
        }

        public Map<String, List<String>> getLookupFieldCriteriaMap() {
            return this.lookupFieldCriteriaMap;
        }

        public Command getBeforeCountCommand() {
            return beforeCountCommand;
        }
    }

    public class SummaryHandler implements SummaryBuilder {
        private Command beforeFetchCommand;
        private Command afterFetchCommand;
        private V3Builder parent;

        private SummaryHandler(V3Builder parent) {
            this.parent = parent;
        }

        public SummaryHandler beforeFetch(Command beforeFetchCommand) {
            this.beforeFetchCommand = beforeFetchCommand;
            return this;
        }

        @Override
        public SummaryHandler afterFetch(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
            return this;
        }

        @Override
        public UpdateBuilder update() {
            return this.parent.update();
        }

        @Override
        public CreateBuilder create() {
            return this.parent.create();
        }

        @Override
        public DeleteBuilder delete() {
            return this.parent.delete();
        }

        @Override
        public ListBuilder list() {
            return this.parent.list();
        }

        @Override
        public V3Config build() {
            return this.parent.build();
        }

        public Command getBeforeFetchCommand() {
            return beforeFetchCommand;
        }

        public Command getAfterFetchCommand() {
            return afterFetchCommand;
        }
    }

}
