package com.accept.jugo.Controller;

import com.alibaba.fastjson.JSONObject;

import com.accept.jugo.service.MoranAcceptService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping(value = "/accept/")
@Api(value = "Sample", description = "MoranI Accept",produces = MediaType.ALL_VALUE)
@RestController
//@RequestMapping(value = "/accept/")
public class MoranController {

    @Autowired
    private MoranAcceptService moranService;

    @ResponseBody
    @PostMapping(value = "/startAccept")
    @ApiOperation(value = "测试-接收方接收方法", httpMethod = "POST", notes = "提供发起计算数据")
    public String startAccept(
            @ApiParam(required = true, name = "id", value = "id is the only 唯一标识,双方必须一致") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "input", value = "输入计算参数1") @RequestParam(name = "input", required = true) String input,
            //@ApiParam(required = false, name = "weight", value = "鏉冮噸鐭╅樀") @RequestParam(name = "weight", required = false) String weight,
            @ApiParam(required = false, name = "roomId", value = "输入房间号，不输入则默认为不检验规则") @RequestParam(name = "roomId", required = false) String roomId,
            @ApiParam(required = false, name = "user", value = "输入用户2（接受者，房间号输入本字段有效）") @RequestParam(name = "user", required = false) String user)
            throws Exception {
        if(StringUtils.isNotBlank(roomId) && StringUtils.isBlank(user)){
            System.out.println("参数错误");
            return JSONObject.toJSONString("'result':'参数错误'");
        }
        return moranService.startAccept(id, input,roomId, user);
    }

    @ResponseBody
    @PostMapping(value = "/queryResult")
    @ApiOperation(value = "测试-查询结果方法", httpMethod = "POST", notes = "查询计算结果")
    public String queryResult(
            @ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id)
            throws Exception {
        //return "鏌ヨ鎴愬姛";
        return moranService.query(id);
    }


}

