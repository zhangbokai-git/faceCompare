package org.jeecg.modules.keyclock.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jeecg.config.KeyCloakConfig;
import org.jeecg.config.SubjectKeyCloakConfig;
import org.jeecg.modules.keyclock.service.KeyCloakService;
import org.jeecg.modules.keyclock.service.PermissionSwitchService;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.mapper.SysRoleMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;

@Service
@Slf4j
public class KeyCloakServiceImpl implements KeyCloakService {

    @Autowired
    private PermissionSwitchService permissionSwitchService;

    @Autowired
    private SubjectKeyCloakConfig subjectKeyCloakConfig;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    private static KeyCloakConfig keyCloakConfig;

    public KeyCloakServiceImpl(@Autowired KeyCloakConfig keyCloakConfig) {
        this.keyCloakConfig = keyCloakConfig;
    }

    public Keycloak getInstance() {
        return keycloakss.getKeycloak();
    }

    static class keycloakss {
        public static Keycloak getKeycloak() {
            Keycloak instance = null;
            if (instance == null) {
                instance = Keycloak.getInstance(
                        keyCloakConfig.getAuthServerUrl(),
                        keyCloakConfig.getRealm(),
                        keyCloakConfig.getUsername(),
                        keyCloakConfig.getPassword(),
                        keyCloakConfig.getClientId());
            }
            return instance;
        }
    }


    @Override
    public ClientRepresentation getClient(String reaml, String cliendId) {
        Keycloak keycloak = getInstance();
        return keycloak.realms().realm(reaml).clients().findByClientId(cliendId).get(0);
    }

    @Override
    public ClientResource getClientResource(String realm, String clientId) {
        Keycloak keycloak = getInstance();

        ClientsResource clients = keycloak.realms().realm(realm).clients();

        List<ClientRepresentation> clientRepresentation = clients.findByClientId(clientId);

//        if (clientRepresentation != null && clientRepresentation.size() == 1) {
//            throw new RuntimeException(String.format("client:%s必须存在", clientId));
//        }

        return clients.get(clientRepresentation.get(0).getId());
    }

    @Override
    public List<String> createClientRole(String selectedRoles) {
        String[] split = selectedRoles.split(",");
        List<String> list = new LinkedList<>();
        ClientResource clientResource = null;
        if (split != null && split.length > 0) {
            RealmResource realm = getInstance().realms().realm(subjectKeyCloakConfig.getRealm());
            clientResource = realm.clients().get(realm.clients().findByClientId(subjectKeyCloakConfig.getResource()).get(0).getId());
            for (String roleId : split
                    ) {
                SysRole sysRole = sysRoleMapper.selectById(roleId);
                if (sysRole != null) {
                    RoleRepresentation representation = new RoleRepresentation();
                    //名称和id与jeecg_boot数据库sys_role表数据同步
                    representation.setName(sysRole.getRoleCode());
                    representation.setDescription(sysRole.getDescription());
                    clientResource.roles().create(representation);
                    list.add(sysRole.getRoleCode());
                }
            }
        }
        //创建角色对应的策略，一个角色一个角色策略
        if (list.size() > 0 && clientResource != null) {
            permissionSwitchService.createRolePolicy(clientResource, list);
        }
        return list;
    }

    @Override
    public void resetUserPassword(UserResource userResource, String newPassword, boolean temporary) {
        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(PASSWORD);
        newCredential.setValue(newPassword);
        newCredential.setTemporary(temporary);
        userResource.resetPassword(newCredential);
    }

    @Override
    public void assignClientRoles(UserResource userResource, List<String> roles) {
        String realmName = subjectKeyCloakConfig.getRealm();
        RealmResource realm = getInstance().realms().realm(realmName);
        String clientId = "";
        String clientName = subjectKeyCloakConfig.getResource();
        for (ClientRepresentation clientRepresentation : realm.clients().findAll()) {
            if (clientRepresentation.getClientId().equals(clientName)) {
                clientId = clientRepresentation.getId();
            }
        }
        if (!clientId.isEmpty()) {
            ClientResource clientResource = realm.clients().get(clientId);

            List<RoleRepresentation> roleRepresentations = new ArrayList<>();
            for (String roleName : roles) {
                RoleRepresentation role = clientResource.roles().get(roleName).toRepresentation();
                roleRepresentations.add(role);
            }

            log.info("assigning role: " + roles.toString() + " to user: \""
                    + userResource.toRepresentation().getUsername() + "\" of client: \""
                    + clientName + "\" in realm: \"" + realmName + "\"");
            userResource.roles().clientLevel(clientId).add(roleRepresentations);
        } else {
            log.warn("client with name " + clientName + " doesn't exist in realm " + realmName);
        }
    }

    @Override
    public void assignRealmRoles(UserRepresentation user, RoleRepresentation role) {
        getInstance().realms().realm(subjectKeyCloakConfig.getRealm()).users().get(user.getId())
                .roles().realmLevel().add(Arrays.asList(role));
        log.info("assigning roles " + role.getName() + " to user: \""
                + user.getUsername() + "\" in realm: \"" + getInstance().realms().realm(subjectKeyCloakConfig.getRealm()).toRepresentation().getRealm() + "\"");
    }

    @Override
    public UserRepresentation findUserByUsername(RealmResource realm, String username) {
        UserRepresentation user = null;
        List<UserRepresentation> ur = realm.users().search(username, null, null, null, 0, Integer.MAX_VALUE);
        if (ur.size() == 1) {
            user = ur.get(0);
        }

        if (ur.size() > 1) { // try to be more specific
            for (UserRepresentation rep : ur) {
                if (rep.getUsername().equalsIgnoreCase(username)) {
                    return rep;
                }
            }
        }

        return user;
    }

    @Override
    public ResourceRepresentation createResource(ClientResource clientResource, String roleId, String permissionIds) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole != null && !sysRole.equals("")) {
            String roleName = sysRole.getRoleCode();
            String[] split = permissionIds.split(",");
            //创建资源
            ResourceRepresentation resourceRepresentation = new ResourceRepresentation();
            Set<String> uris = new HashSet<>(split.length);
            resourceRepresentation.setName(roleName);
            if (split.length > 0) {
                for (String pid : split
                        ) {
                    SysPermission sysPermission = sysPermissionMapper.selectById(pid);
                    uris.add(sysPermission.getUrl());
                }
            }
            resourceRepresentation.setUris(uris);
            Response response = clientResource.authorization().resources().create(resourceRepresentation);
            if (response.getStatus() == HttpResponseCodes.SC_CREATED) {
                return resourceRepresentation;
            }
        }
        return null;
    }

    @Override
    public void createPermission(ClientResource clientResource, String roleId) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole != null && !sysRole.equals("")) {
            String roleName = sysRole.getRoleCode();
            ResourcePermissionRepresentation representation = new ResourcePermissionRepresentation();
            //根据名称找需要关联成权限的资源和策略
            Set<String> resources = new HashSet<>();
            Set<String> policies = new HashSet<>();
            ResourceRepresentation resourceByName = findResourceByName(clientResource, roleName);
            RolePolicyRepresentation rolePolicyRepresentation = findRolePolicyByName(clientResource, roleName);
            resources.add(resourceByName.getName());
            policies.add(rolePolicyRepresentation.getName());
            representation.setName(roleName + "_" + roleName);
            representation.setResources(resources);
            representation.setPolicies(policies);
            clientResource.authorization().permissions().resource().create(representation);
        }
    }

    @Override
    public ResourceRepresentation findResourceByName(ClientResource clientResource, String name) {
        ResourceRepresentation resourceRepresentation = clientResource.authorization().resources().findByName(name).get(0);
        if (resourceRepresentation != null && !resourceRepresentation.equals("")) {
            return resourceRepresentation;
        }
        return null;
    }

    @Override
    public RolePolicyRepresentation findRolePolicyByName(ClientResource clientResource, String name) {
        RolePolicyRepresentation rolePolicyRepresentation = clientResource.authorization().policies().role().findByName(name);
        if (rolePolicyRepresentation != null && !rolePolicyRepresentation.equals("")) {
            return rolePolicyRepresentation;
        }
        return null;
    }

    @Override
    public void updateResource(ClientResource clientResource, String roleId, String permissionIds) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole != null && !sysRole.equals("")) {
            ResourceRepresentation resourceRepresentation = clientResource.authorization().resources().findByName(sysRole.getRoleCode()).get(0);
            String[] split = permissionIds.split(",");
            Set<String> uris = new HashSet<>(split.length);
            if (split.length > 0) {
                for (String pid : split
                        ) {
                    SysPermission sysPermission = sysPermissionMapper.selectById(pid);
                    uris.add(sysPermission.getUrl());
                }
            }
            resourceRepresentation.setUris(uris);
            clientResource.authorization().resources().resource(resourceRepresentation.getId()).update(resourceRepresentation);
        }
    }

    @Override
    public void updateUserRole(UserResource userResource, List<String> roles) {
        ClientResource clientResource = getClientResource(subjectKeyCloakConfig.getRealm(), subjectKeyCloakConfig.getResource());
        List<RoleRepresentation> roleRepresentations = new ArrayList<>();
        for (String roleName : roles) {
            RoleRepresentation role = clientResource.roles().get(roleName).toRepresentation();
            roleRepresentations.add(role);
        }
        RoleScopeResource roleScopeResource = userResource.roles().clientLevel(clientResource.toRepresentation().getId());
        roleScopeResource.remove(roleScopeResource.listAll());
        roleScopeResource.add(roleRepresentations);
        userResource.update(userResource.toRepresentation());
    }

    @Override
    public void deleteUser(String userName) {
        Keycloak instance = getInstance();
        RealmResource realm = instance.realms().realm(subjectKeyCloakConfig.getRealm());
        realm.users().delete(realm.users().search(userName).get(0).getId());
    }

    @Override
    public void deleteBatchUser(List<SysUser> sysUsers) {
        Keycloak instance = getInstance();
        RealmResource realm = instance.realms().realm(subjectKeyCloakConfig.getRealm());
        for (SysUser user : sysUsers
                ) {
            realm.users().delete(realm.users().search(user.getUsername()).get(0).getId());
        }
    }

    @Override
    public void deleteRole(ClientResource clientResource, String roleCode) {
        clientResource.roles().deleteRole(roleCode);
    }

    @Override
    public void deleteBatchRoles(ClientResource clientResource, List<SysRole> sysRoles) {
        for (SysRole role : sysRoles
                ) {
            clientResource.roles().deleteRole(role.getRoleCode());
        }
    }

    @Override
    public void deleteResource(ClientResource clientResource, String roleCode) {
        ResourcesResource resources = clientResource.authorization().resources();
        List<ResourceRepresentation> resourceRepresentations = resources.findByName(roleCode);
        if (resourceRepresentations != null && resourceRepresentations.size() > 0) {
            clientResource.authorization().resources().resource(resourceRepresentations.get(0).getId()).remove();
        }
    }

    @Override
    public void deleteBatchResource(ClientResource clientResource, List<SysRole> sysRoles) {
        ResourcesResource resources = clientResource.authorization().resources();
        for (SysRole role : sysRoles
                ) {
            resources.resource(resources.findByName(role.getRoleCode()).get(0).getId()).remove();
        }
    }


}
