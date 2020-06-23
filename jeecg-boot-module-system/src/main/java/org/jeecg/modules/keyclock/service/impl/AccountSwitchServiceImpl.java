package org.jeecg.modules.keyclock.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jeecg.config.SubjectKeyCloakConfig;
import org.jeecg.modules.keyclock.service.AccountSwitchService;
import org.jeecg.modules.keyclock.service.KeyCloakService;
import org.jeecg.modules.keyclock.util.ApiUtil;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysRoleMapper;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
@Slf4j
public class AccountSwitchServiceImpl implements AccountSwitchService {

    @Autowired
    private KeyCloakService keyCloakService;
    @Autowired
    private SubjectKeyCloakConfig subjectKeyCloakConfig;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public boolean createKeyCloakUser(SysUser user, String password, String selectedRoles) {
        try {
            UserResource userResource = createUser(user.getUsername(), user.getEmail());
            if (userResource != null) {
                //设置用户密码
                keyCloakService.resetUserPassword(userResource, password, false);
                //用户绑定客户端角色
                if (selectedRoles != null && !selectedRoles.equals("")) {
                    keyCloakService.assignClientRoles(userResource, getRoleName(selectedRoles));
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<String> getRoleName(String selectedRoles) {
        String[] split = selectedRoles.split(",");
        List<String> list = new LinkedList<>();
        if (split != null && split.length > 0) {
            for (String roleId : split
                    ) {
                SysRole sysRole = roleMapper.selectById(roleId);
                if (sysRole != null) {
                    list.add(sysRole.getRoleCode());
                }
            }
        }
        return list;
    }

    @Override
    public boolean updateKeyCloakUser(SysUser user, String selectedRoles) {
        RealmResource realm = keyCloakService.getInstance().realms().realm(subjectKeyCloakConfig.getRealm());
        UserRepresentation userByUsername = keyCloakService.findUserByUsername
                (realm, user.getUsername());
        try {
            keyCloakService.updateUserRole(realm.users().get(userByUsername.getId()), getRoleName(selectedRoles));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteKeyCloakUser(String id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser!=null && !sysUser.equals("")){
            try {
                keyCloakService.deleteUser(sysUser.getUsername());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean deleteBatchKeyCloakUser(String ids) {
        String[] split = ids.split(",");
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(Arrays.asList(split));
        keyCloakService.deleteBatchUser(sysUsers);
        return false;
    }

    @Override
    public void changePassword(SysUser user) {
        RealmResource realm = keyCloakService.getInstance().realms().realm(subjectKeyCloakConfig.getRealm());
        UserRepresentation userByUsername = keyCloakService.findUserByUsername
                (realm, user.getUsername());
        keyCloakService.resetUserPassword(realm.users().get(userByUsername.getId()), user.getPassword(), false);
    }

    /**
     * 创建keycloak用户
     *
     * @param username
     * @param email
     * @return
     */
    private UserResource createUser(String username, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setRequiredActions(Collections.emptyList());
        user.setEnabled(true);
        RealmResource realm = keyCloakService.getInstance().realms().realm(subjectKeyCloakConfig.getRealm());
        Response response = realm.users().create(user);
        String createdId = ApiUtil.getCreatedId(response);
        if (response != null && response.getStatus() == HttpResponseCodes.SC_CREATED) {
            response.close();
            UserResource userResource = realm.users().get(createdId);
            return userResource;
        }
        return null;
    }


}
