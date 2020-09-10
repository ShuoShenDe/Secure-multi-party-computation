package com.accept.jugo.Controller;


import com.accept.jugo.conf.Const;
import com.accept.jugo.database.PostRequest;
import com.accept.jugo.database.SelectData;
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

import java.util.*;
import java.lang.String;
import com.accept.jugo.service.*;
import com.accept.jugo.database.DataProcessing;

@Controller
@RequestMapping(value = "/send/")
@Api(value = "Sample", description = "Server for front end", produces = MediaType.ALL_VALUE)
@RestController
//@RequestMapping(value = "/accept/")
public class ServerController {

    @Autowired
    private MoranAcceptService moranserver;

    @ResponseBody
    @PostMapping(value = "/sendParam")
    @ApiOperation(value = "server for website", httpMethod = "POST", notes = "accept require from website")
    public String queryResult(
            @ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "filename", value = "filename") @RequestParam(name = "filename", required = true) String filename,
            @ApiParam(required = false, name = "roomId", value = "输入房间号，不输入则默认为不检验规则") @RequestParam(name = "roomId", required = false) String roomId,
            @ApiParam(required = false, name = "user", value = "输入用户2（接受者，房间号输入本字段有效）") @RequestParam(name = "user", required = false) String user
    )
            throws Exception {

        if (StringUtils.isNotBlank(roomId) && StringUtils.isBlank(user)) {
            System.out.println("参数错误");
            return JSONObject.toJSONString("'result':'参数错误'");
        }

        String sql = "SELECT value FROM " + filename + ";";
        List<Double> selectdata = SelectData.selectdatabase(sql, "postgis");
        //standardization the select data
        double sumsqure = DataProcessing.sumsqurestandardization(selectdata);
        List<Integer> standdaata = DataProcessing.standardization(selectdata);
        String acceptRequest = "";
        String MoranI = "";
        double SD = 0.000;

        JSONObject result = new JSONObject();

        String cid = "";
        int i = 0;
        for (; i < 50; i++) {
            String input = standdaata.toString().substring(1, standdaata.toString().length() - 1).replace(" ", "");
            System.out.println("standdaata input value" + input);
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }
            acceptRequest = moranserver.startAccept(cid, input, "", "");

            Collections.shuffle(standdaata);
        }



        result.clear();
        System.out.println("sumsqure = "+sumsqure );
        result.put("sumsqure", sumsqure);

        result.put("SD", SD);

        System.out.println(acceptRequest);
        return acceptRequest;
    }


}
