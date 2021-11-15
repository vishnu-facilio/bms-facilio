package com.facilio.accounts.impl;

import com.facilio.accounts.bean.SecurityBean;
import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;

public class SecurityBeanImpl implements SecurityBean {
    @Override
    public NodeError validate(Executor executor) throws Exception {
        return executor.validatePostAuth();
    }
}
