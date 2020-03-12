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

    public V3Config(String modulename, Class bean) {
        this.beanClass = bean;
        this.moduleName = modulename;
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

    public V3Builder build() {
        return this;
    }

    private class CreateHandler implements CreateBuilder {
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
        public V3Builder build() {
           return this.parent.build();
        }
    }

    private class UpdateHandler implements UpdateBuilder {
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
        public V3Builder build() {
            return this.parent.build();
        }


    }

    private class DeleteHandler implements DeleteBuilder {
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
        public V3Builder build() {
            return this.parent.build();
        }
    }



    private class ListHandler implements ListBuilder {
        private Command criteriaCommand;
        private Command afterFetchCommand;
        private V3Builder parent;

        private ListHandler(V3Builder parent) {
            this.parent = parent;
        }

        @Override
        public ListHandler criteria(Command criteriaCommand) {
            this.criteriaCommand = criteriaCommand;
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
        public V3Builder build() {
            return this.parent.build();
        }
    }

    private class SummaryHandler implements SummaryBuilder {
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
        public V3Builder build() {
            return this.parent.build();
        }
    }

}
