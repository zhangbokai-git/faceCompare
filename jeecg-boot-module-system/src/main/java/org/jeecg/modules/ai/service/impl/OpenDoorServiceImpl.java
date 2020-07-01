package org.jeecg.modules.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.ai.entity.OpenDoor;
import org.jeecg.modules.ai.mapper.OpenDoorMapper;
import org.jeecg.modules.ai.service.IOpenDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: open_door
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@Service
public class OpenDoorServiceImpl extends ServiceImpl<OpenDoorMapper, OpenDoor> implements IOpenDoorService {

    @Autowired
    private OpenDoorMapper openDoorMapper;

    @Override
    public Integer userOpenDoor(String groupid, String userid) {
        OpenDoor openDoor = new OpenDoor();
        openDoor.setGroupid(groupid);
        openDoor.setUserid(userid);
        Date date = new Date();
        openDoor.setCreatedtime(date);
        int count = openDoorMapper.insert(openDoor);
        return count;
    }
}
