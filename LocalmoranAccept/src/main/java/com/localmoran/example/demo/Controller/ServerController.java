package com.localmoran.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.localmoran.example.demo.database.DataProcessing;
import com.localmoran.example.demo.database.SelectData;
import com.localmoran.example.demo.service.LocalMoranService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/send/")
@Api(value = "Sample", description = "Server for front end", produces = MediaType.ALL_VALUE)
@RestController
//@RequestMapping(value = "/accept/")
public class ServerController {

    @Autowired
    private LocalMoranService localmoran;

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

        List<Double> moranRecord = new ArrayList<Double>();
        String sql = "SELECT value FROM " + filename + ";";
        List<Double> selectdata = SelectData.selectdatabase(sql, "postgis");
        //standardization the select data
        double sumsqure = DataProcessing.sumsqurestandardization(selectdata);
        List<Integer> standdaata = DataProcessing.standardization(selectdata);
        //Map<String, Object> acceptRequest = null;
        String acceptRequest = "";

        double SD = 0.000;

        JSONObject result = new JSONObject();

        String cid = "";
        int i = 0;
        for (; i < 100; i++) {
            String input = standdaata.toString().substring(1, standdaata.toString().length() - 1).replace(" ", "");
            System.out.println("standdaata input value" + input);
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }
            acceptRequest = localmoran.startAccept(cid, input, roomId, user);
            //acceptRequest = moranService.startAccept(id, input, "", "");
            Collections.shuffle(standdaata);
        }


        result.clear();
        System.out.println("sumsqure = "+sumsqure );
        result.put("sumsqure", sumsqure);

        result.put("SD", SD);

        System.out.println(acceptRequest);
        return acceptRequest;
    }



    @ResponseBody
    @PostMapping(value = "/queryResult")
    @ApiOperation(value = "测试-查询结果方法", httpMethod = "POST", notes = "查询计算结果")
    public String queryP(
            @ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id)
            throws Exception {
        //return "鏌ヨ鎴愬姛";
         localmoran.query(id);
        return "null";
    }


}
