package org.jeecg.modules.keyclock.service;

import org.jeecg.modules.system.entity.SysUser;

/**
 * keyclock和jeecgboot账号转换接口，主要用途是把jeecgboot注册的账号转为keyclock系统上的账号
 */
public interface AccountSwitchService {

      /**
       * 新增用户转存jeecg_boot2keycloak
       * @param user
       * @param password
       * @param selectedRoles
       * @return
       */
      boolean createKeyCloakUser(SysUser user, String password, String selectedRoles);

      /**
       * 修改用户信息，目前只能修改角色
       * @param user
       * @param selectedRoles
       * @return
       */
      boolean updateKeyCloakUser(SysUser user, String selectedRoles);

      /**
       * 删除用户转储jeecg_boot2keycloak
       * @param id
       * @return
       */
      boolean deleteKeyCloakUser(String id);

      /**
       * 批量删除用户
       * @param ids
       * @return
       */
      boolean deleteBatchKeyCloakUser(String ids);

      /**
       * 修改用户密码
       * @param user
       */
      void changePassword(SysUser user);
}
