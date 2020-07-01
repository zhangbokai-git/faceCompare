package org.jeecg.modules.ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.ai.entity.StoreIp;
import org.jeecg.modules.ai.service.IStoreIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: store_ip
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@RestController
@RequestMapping("/knowledge/storeIp")
@Slf4j
public class StoreIpController extends JeecgController<StoreIp, IStoreIpService> {
	@Autowired
	private IStoreIpService storeIpService;
	


}
