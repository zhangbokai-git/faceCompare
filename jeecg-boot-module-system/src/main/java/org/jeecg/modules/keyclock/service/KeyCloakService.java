package org.jeecg.modules.keyclock.service;


import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysUser;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

import java.util.List;

/**
 * keyclock java docx
 */
public interface KeyCloakService {

    /**
     * 获取keycloak
     * @return
     */
    Keycloak getInstance();

    /**
     * 获取指定域的指定客户端
     * @param reaml 域名
     * @param cliendId 客户端id。name, not uuid
     * @return
     */
    ClientRepresentation getClient(String reaml,String cliendId);

    /**
     * 获取keycloak指定域下的客户端资源
     */
    ClientResource getClientResource(String realm,String clientId);

    /**
     * 创建客户端角色
     * @param selectedRoles
     * @return
     */
    List<String> createClientRole(String selectedRoles);

    /**
     * 重置用户密码
     *
     * @param userResource
     * @param newPassword
     * @param temporary
     */
    void resetUserPassword(UserResource userResource, String newPassword, boolean temporary);

    /**
     * 绑定客户端角色
     * @param userResource
     * @param roles
     */
    void assignClientRoles(UserResource userResource, List<String> roles);

    /**
     * 绑定域角色
     * @param user
     * @param role
     */
    void assignRealmRoles(UserRepresentation user, RoleRepresentation role);

    /**
     * 查找用户
     * @param realm
     * @param username
     * @return
     */
    UserRepresentation findUserByUsername(RealmResource realm, String username);

    /**
     * 创建资源
     * @param clientResource
     * @param roleId
     * @param permissionIds
     * @return
     */
    ResourceRepresentation createResource(ClientResource clientResource, String roleId, String permissionIds);

    /**
     * 创建权限
     * @param clientResource
     * @param roleId
     */
    void createPermission(ClientResource clientResource, String roleId);

    /**
     * 根据名称查询资源
     * @param name
     * @param clientResource
     * @return
     */
    ResourceRepresentation findResourceByName(ClientResource clientResource,String name);

    /**
     * 根据名称查询策略
     * @param name
     * @param clientResource
     * @return
     */
    RolePolicyRepresentation findRolePolicyByName(ClientResource clientResource, String name);

    /**
     * 更新资源
     * @param clientResource
     * @param roleId
     * @param permissionIds
     */
    void updateResource(ClientResource clientResource, String roleId, String permissionIds);

    /**
     * 修改用户角色
     * @param userResource
     * @param roles
     */
    void updateUserRole(UserResource userResource,List<String> roles);

    /**
     * 删除用户
     * @param userName
     */
    void deleteUser(String userName);

    /**
     * 批量删除用户
     * @param sysUsers
     */
    void deleteBatchUser(List<SysUser> sysUsers);

    /**
     * 删除角色
     * @param clientResource
     * @param roleCode
     */
    void deleteRole(ClientResource clientResource, String roleCode);

    /**
     * 批量删除角色
     * @param clientResource
     * @param sysRoles
     */
    void deleteBatchRoles(ClientResource clientResource, List<SysRole> sysRoles);

    /**
     * 删除资源
     * @param clientResource
     * @param roleCode
     */
    void deleteResource(ClientResource clientResource, String roleCode);

    /**
     * 批量删除资源
     * @param clientResource
     * @param sysRoles
     */
    void deleteBatchResource(ClientResource clientResource, List<SysRole> sysRoles);
}
