package com.moranstart.jugo.controller;

import com.alibaba.fastjson.JSONObject;
import com.moranstart.jugo.dto.ResultDto;
import com.moranstart.jugo.service.MoranStartService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/start/")
public class MoranStartController {

    @Autowired
    private MoranStartService moranService;

    @ResponseBody
    @PostMapping(value = "/startTask")
    @ApiOperation(value = "测试-发起方调用方法", httpMethod = "POST", notes = "提供接收计算")
    public String startCompareTask(
            @ApiParam(required = true, name = "id", value = "唯一标识,双方必须一致") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "input", value = "输入计算参数1") @RequestParam(name = "input", required = true) String input,
            @ApiParam(required = true, name = "type", value = "计算发起方/接收方") @RequestParam(name = "type", required = false, defaultValue = "1") Integer type,
            @ApiParam(required = false, name = "roomId", value = "输入房间号，不输入则默认用户不指定") @RequestParam(name = "roomId", required = false) String roomId,
            @ApiParam(required = false, name = "user1", value = "输入用户1（发起者，房间号输入本字段有效）") @RequestParam(name = "user1", required = false) String user1,
            @ApiParam(required = false, name = "user2", value = "输入用户2（接受者，房间号输入本字段有效）") @RequestParam(name = "user2", required = false) String user2)
            throws Exception {
    	if(StringUtils.isNotBlank(roomId) && (StringUtils.isBlank(user1) || StringUtils.isBlank(user2))){
			ResultDto resultDto = new ResultDto(1, "参数错误，roomId:" + roomId + ",user1:" + user1 + ",user2:" + user2);
			return JSONObject.toJSONString(resultDto);
    	}
        return moranService.startCompareTask(id, input, type, roomId,user1,user2);
    }

    @ResponseBody
    @PostMapping(value = "/queryResult")
    @ApiOperation(value = "测试-查询结果方法", httpMethod = "POST", notes = "查询计算结果")
    public String queryResult(
            @ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id)
            throws Exception {
        return moranService.query(id);
    }

}
