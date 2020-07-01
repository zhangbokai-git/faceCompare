package org.jeecg.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: store_ip
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@Data
@TableName("store_ip")
public class StoreIp implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主键id*/
	@TableId(type = IdType.ID_WORKER_STR)
    private java.lang.Integer id;
	/**group_id*/
	@Excel(name = "group_id", width = 15)
    private java.lang.String groupid;
	/**设备ip*/
	@Excel(name = "设备ip", width = 15)
    private java.lang.String ip;
}
