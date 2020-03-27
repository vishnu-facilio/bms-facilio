package com.facilio.v3.V3Builder;

import org.apache.commons.chain.Command;

public class V3Config implements V3Builder {
    private String moduleName;
    private Class beanClass;
    private CreateHandler createHandler;
    private UpdateHandler updateHandler;
    private DeleteHandler deleteHandler;
    private ListHandler listHandler;
    private SummaryHandler summaryHandler;

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

    public V3Config(Class bean) {
        this.beanClass = bean;
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
        private V3Builder parent;

        private ListHandler(V3Builder parent) {
            this.parent = parent;
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
    }

    public class SummaryHandler implements SummaryBuilder {
        private Command afterFetchCommand;
        private V3Builder parent;

        private SummaryHandler(V3Builder parent) {
            this.parent = parent;
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

        public Command getAfterFetchCommand() {
            return afterFetchCommand;
        }

        public void setAfterFetchCommand(Command afterFetchCommand) {
            this.afterFetchCommand = afterFetchCommand;
        }
    }

}
