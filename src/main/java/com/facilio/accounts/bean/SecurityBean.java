package com.facilio.accounts.bean;

import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;

public interface SecurityBean {
    NodeError validate(Executor executor) throws Exception;
}
