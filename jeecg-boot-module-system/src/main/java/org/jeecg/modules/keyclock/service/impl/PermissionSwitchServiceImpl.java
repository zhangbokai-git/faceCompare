package org.jeecg.modules.keyclock.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jeecg.config.SubjectKeyCloakConfig;
import org.jeecg.modules.keyclock.service.KeyCloakService;
import org.jeecg.modules.keyclock.service.PermissionSwitchService;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.mapper.SysRoleMapper;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PermissionSwitchServiceImpl implements PermissionSwitchService {

    @Autowired
    private KeyCloakService keyCloakService;
    @Autowired
    private SubjectKeyCloakConfig subjectKeyCloakConfig;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public void createRolePolicy(ClientResource clientResource, List<String> list) {

        if (clientResource == null) {
            clientResource = keyCloakService.getClientResource(subjectKeyCloakConfig.getRealm(), subjectKeyCloakConfig.getResource());
        }

        for (String info : list
                ) {
            RolePolicyRepresentation rolePolicyRepresentation = new RolePolicyRepresentation();
            RolePolicyRepresentation.RoleDefinition roleDefinition = new RolePolicyRepresentation.RoleDefinition();

            //策略关联角色为对应的客户端角色
            String id = clientResource.roles().get(info).toRepresentation().getId();
            roleDefinition.setId(id);
            roleDefinition.setRequired(false);
            Set<RolePolicyRepresentation.RoleDefinition> roles = new HashSet<>();
            roles.add(roleDefinition);
            rolePolicyRepresentation.setRoles(roles);

            //策略名称为角色名称
            rolePolicyRepresentation.setName(info);
            rolePolicyRepresentation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
            rolePolicyRepresentation.setLogic(Logic.POSITIVE);

            clientResource.authorization().policies().role().create(rolePolicyRepresentation);
            log.info("create rolepolicy name: " + info + " in client: " + clientResource.toRepresentation().getName());
        }
    }

    @Override
    public void saveRolePermission(ClientResource clientResource, String roleId, String permissionIds, String lastPermissionIds) {
        if (clientResource == null) {
            clientResource = keyCloakService.getClientResource(subjectKeyCloakConfig.getRealm(), subjectKeyCloakConfig.getResource());
        }

        //新增资源并新增权限
        if (lastPermissionIds.equals("")) {
            ResourceRepresentation resource = keyCloakService.createResource(clientResource, roleId, permissionIds);
            if (resource != null) {
                keyCloakService.createPermission(clientResource, roleId);
            }
        } else {//更新资源
            keyCloakService.updateResource(clientResource, roleId, permissionIds);
        }
    }

    @Override
    public void deleteRole(String id) {
        SysRole sysRole = sysRoleMapper.selectById(id);
        if (sysRole != null && !sysRole.equals("")) {
            ClientResource clientResource = keyCloakService.getClientResource
                    (subjectKeyCloakConfig.getRealm(), subjectKeyCloakConfig.getResource());
            keyCloakService.deleteRole(clientResource, sysRole.getRoleCode());
            //删除资源
            keyCloakService.deleteResource(clientResource,sysRole.getRoleCode());
        }
    }

    @Override
    public void deleteBatchRole(String ids) {
        List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(Arrays.asList(ids.split(",")));
        if (sysRoles != null && sysRoles.size() > 0) {
            ClientResource clientResource = keyCloakService.getClientResource
                    (subjectKeyCloakConfig.getRealm(), subjectKeyCloakConfig.getResource());
            keyCloakService.deleteBatchRoles(clientResource,sysRoles);
            keyCloakService.deleteBatchResource(clientResource,sysRoles);
        }
    }


}
