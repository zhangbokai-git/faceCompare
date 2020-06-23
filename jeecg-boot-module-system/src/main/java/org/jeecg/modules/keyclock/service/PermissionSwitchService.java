package org.jeecg.modules.keyclock.service;

import org.keycloak.admin.client.resource.ClientResource;

import java.util.List;

/**
 * 权限管理转换
 */
public interface PermissionSwitchService {

    /**
     * 创建角色对应的角色策略
     */
    void createRolePolicy(ClientResource clientResource, List<String> list);

    /**
     * 角色授权
     * @param roleId
     * @param permissionIds
     * @param lastPermissionIds
     */
    void saveRolePermission(ClientResource clientResource,String roleId,String permissionIds,String lastPermissionIds);

    /**
     * 删除角色
     * @param id
     */
    void deleteRole(String id);

    /**
     * 批量删除角色
     * @param ids
     */
    void deleteBatchRole(String ids);


}
