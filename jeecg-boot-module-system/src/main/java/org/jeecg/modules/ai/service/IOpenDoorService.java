package org.jeecg.modules.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.ai.entity.OpenDoor;

/**
 * @Description: open_door
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
public interface IOpenDoorService extends IService<OpenDoor> {

    Integer userOpenDoor(String groupid, String userid);
}
