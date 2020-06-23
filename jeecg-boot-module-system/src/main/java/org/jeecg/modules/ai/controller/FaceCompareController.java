package org.jeecg.modules.ai.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.ai.service.IFaceCompareService;
import org.jeecg.modules.ai.vo.FaceCompareDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/faceCompareController")
@Slf4j
public class FaceCompareController {

    @Autowired
    private IFaceCompareService faceCompareService;

    /**
     * 人脸比对查询接口
     * @param
     * @return
     */
    @ResponseBody
    @ApiOperation("人脸比对查询接口")
    @PostMapping("/selectFaceCompare")
    public Result<String> selectFaceCompare(@RequestBody FaceCompareDTO faceCompareDTO){
        Result<String> result = new Result<>();
        try {
            String str = faceCompareService.selectFaceCompare(faceCompareDTO.getGroup(),faceCompareDTO.getBase64());
            result.setResult(str);
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
