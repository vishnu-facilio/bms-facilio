package com.facilio.beans;


import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;

import java.util.List;

public interface UserScopeBean {

    public List<ScopingConfigCacheContext> getScopingConfig(long scopingId) throws Exception;

    public void deleteScopingConfigForId(long scopingConfigId) throws Exception;

    public void addScopingConfigForApp(List<ScopingConfigContext> scoping) throws Exception;

    public void deleteScopingConfig(long scopingId) throws Exception;

}