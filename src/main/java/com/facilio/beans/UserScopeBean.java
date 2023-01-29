package com.facilio.beans;


import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;

import java.util.List;

public interface UserScopeBean {

    public List<ScopingConfigCacheContext> getScopingConfig(long scopingId) throws Exception;

    public void deleteScopingConfigForId(long scopingConfigId) throws Exception;

    public void addScopingConfigForApp(List<ScopingConfigContext> scoping) throws Exception;

    public void deleteScopingConfig(long scopingId) throws Exception;

    public void addUserScoping(ScopingContext userScoping) throws Exception;

    public void updateUserScoping(ScopingContext userScoping) throws Exception;

    public Long getUserScopingCount(Long appId, String searchQuery) throws Exception;

    public List<ScopingContext> getUserScopingList(Long appId, String searchQuery, int page, int perPage) throws Exception;

    public void deleteUserScoping(Long userScopingId) throws Exception;

    public void updateUserScopingStatus(Long scopingId, Boolean status) throws Exception;

    public List<ScopingConfigContext> getUserScopingConfig(Long userScopingId) throws Exception;

    public void updateScopingConfigForUserScoping(List<ScopingConfigContext> userScopingConfigList, Long userScopingId) throws Exception;

    }