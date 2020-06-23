package org.jeecg.modules.ai.service.impl;


import com.baidu.aip.face.AipFace;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.service.IFaceCompareService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class FaceCompareServiceImpl implements IFaceCompareService {

    //设置APPID/AK/SK
    public static final String APP_ID = "19357818";

    public static final String API_KEY = "UYzSDpuMN4b1vfbH64YHSHGZ";

    public static final String SECRET_KEY = "Fbwa8lcYBG11fSRW89V4BgvCFkd2zsG6";

    @Override
    public String selectFaceCompare(String group, String base64) {

        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 传入可选参数调用接口
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "3");
        options.put("match_threshold", "70");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("max_user_num", "3");
        String imageType = "BASE64";
        // 人脸检测
        JSONObject res = client.search(base64, imageType,group, options);
        System.out.println(res.toString(2));
        return res.toString(2);
    }
}
