package org.jeecg.modules.ai.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.ai.entity.OpenDoor;
import org.jeecg.modules.ai.service.IOpenDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: open_door
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@RestController
@RequestMapping("/ai/openDoor")
@Slf4j
public class OpenDoorController extends JeecgController<OpenDoor, IOpenDoorService> {
	@Autowired
	private IOpenDoorService openDoorService;

	@ResponseBody
	@ApiOperation("用户开门")
	@GetMapping("/userOpenDoor")
	public Result<Integer> userOpenDoor(@RequestParam(name = "groupid", defaultValue = "")String groupid,
									    @RequestParam(name = "userid", defaultValue = "")String userid){
		Result<Integer> result = new Result<>();
		try {
			Integer count = openDoorService.userOpenDoor(groupid,userid);
			result.setResult(count);
			log.info("查询成功！");
			result.setSuccess(true);
		}catch (Exception e){
			log.error("查询失败！");
			result.setSuccess(false);
			result.error500(e.getMessage());
		}
		return result;
	}
	


}
