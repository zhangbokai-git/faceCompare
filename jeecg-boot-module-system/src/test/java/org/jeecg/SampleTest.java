package org.jeecg;

import java.util.List;

import javax.annotation.Resource;

import org.jeecg.modules.demo.mock.MockController;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.modules.demo.test.mapper.JeecgDemoMapper;
import org.jeecg.modules.demo.test.service.IJeecgDemoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDataLogService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.jeecg.modules.keyclock.service.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleTest {

    @Resource
    private JeecgDemoMapper jeecgDemoMapper;
    @Resource
    private IJeecgDemoService jeecgDemoService;
    @Resource
    private ISysDataLogService sysDataLogService;
    @Autowired
    private KeyCloakService KeyCloakService;
    @Resource
    private MockController mock;
    @Resource
    private AccountSwitchService AccountSwitchService;
    @Autowired
    private PermissionSwitchService permissionSwitchService;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<JeecgDemo> userList = jeecgDemoMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testXmlSql() {
        System.out.println(("----- selectAll method test ------"));
        List<JeecgDemo> userList = jeecgDemoMapper.getDemoByName("Sandy12");
        userList.forEach(System.out::println);
    }

    /**
     * 测试事务
     */
    @Test
    public void testTran() {
        jeecgDemoService.testTran();
    }

    //author:lvdandan-----date：20190315---for:添加数据日志测试----

    /**
     * 测试数据日志添加
     */
    @Test
    public void testDataLogSave() {
        System.out.println(("----- datalog test ------"));
        String tableName = "jeecg_demo";
        String dataId = "4028ef81550c1a7901550c1cd6e70001";
        String dataContent = mock.sysDataLogJson();
        sysDataLogService.addDataLog(tableName, dataId, dataContent);
    }
    //author:lvdandan-----date：20190315---for:添加数据日志测试----


    @Test
    public void createUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername("1219");
        String role = "f6817f48af4fb3af11b9e8bf182f618b,e51758fa916c881624b046d26bd09230";
        String password = "1218";
        KeyCloakService.createClientRole(role);
        AccountSwitchService.createKeyCloakUser(sysUser, password, role);
    }

    @Test
    public void savePermission1() {
        permissionSwitchService.saveRolePermission(null, "f6817f48af4fb3af11b9e8bf182f618b",
                "3f915b2769fc80648e92d04e84ca059d,1a0811914300741f4e11838ff37a1d3a,7593c9e3523a17bca83b8d7fe8a34e58",
                "");
    }

    @Test
    public void savePermission2() {
        permissionSwitchService.saveRolePermission(null, "f6817f48af4fb3af11b9e8bf182f618b",
                "13212d3416eb690c2e1d5033166ff47a,109c78a583d4693ce2f16551b7786786,0620e402857b8c5b605e1ad9f4b89350",
                "3f915b2769fc80648e92d04e84ca059d,1a0811914300741f4e11838ff37a1d3a,7593c9e3523a17bca83b8d7fe8a34e58");
    }

}
