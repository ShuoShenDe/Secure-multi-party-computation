package com.moranstart.jugo.controller;

import com.alibaba.fastjson.JSON;
import com.moranstart.jugo.database.DataProcessing;
import com.moranstart.jugo.database.PointShuffler;
import com.moranstart.jugo.database.PostRequest;
import com.moranstart.jugo.database.SelectData;

import com.moranstart.jugo.service.CrossKStartService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@Api(value = "Sample", description = "Server for front end", produces = MediaType.ALL_VALUE)
@RestController
@RequestMapping(value = "/send/")
//@RequestMapping(value = "/accept/")
public class ServerController {


    @Autowired
    private CrossKStartService crosskservice;

    @ResponseBody
    @PostMapping(value = "/sendParam")
    @ApiOperation(value = "server for website", httpMethod = "POST", notes = "accept require from website")
    public String queryResult(
            @ApiParam(required = true, name = "id", value = "id only value") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "filename", value = "filename") @RequestParam(name = "filename", required = true) String filename,
            @ApiParam(required = true, name = "type", value = "计算发起方/接收方") @RequestParam(name = "type", required = false, defaultValue = "1") Integer type,
            @ApiParam(required = false, name = "roomId", value = "输入房间号，不输入则默认用户不指定") @RequestParam(name = "roomId", required = false) String roomId,
            @ApiParam(required = false, name = "user1", value = "输入用户1（发起者，房间号输入本字段有效）") @RequestParam(name = "user1", required = false) String user1,
            @ApiParam(required = false, name = "user2", value = "输入用户2（接受者，房间号输入本字段有效）") @RequestParam(name = "user2", required = false) String user2)
            throws Exception {
        String acceptRequest = "";
        String select = "";
        String sql = "SELECT x_coordina, y_coordina  FROM " + filename + " LIMIT 10;";
        List<String> selectdata = SelectData.selectdatabase(sql, "postgis");
        String input = selectdata.toString().substring(1, selectdata.toString().length() - 1).replace(" ", "");
        System.out.println("selectdata size" + selectdata.size());

        int[] boundry=PointShuffler.PointBoundary(input);
        int i = 0;
        String cid = "";
        for (; i < 50; i++) {
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }

            crosskservice.startCompareTask(cid, input, type, roomId, user1, user2);
            //PostRequest.request("http://localhost:8086/crossk/startTask?id="+id+"&input="+selectdata+"&type=1", "");
            //System.out.println(acceptRequest);
            input = PointShuffler.PointShuffler(selectdata.size(),boundry);
        }

        Thread.sleep(100000);

        for (int j = 0; j < i; j++) {
            if (j != 0) {
                cid = id + j;
            } else {
                cid = id;
            }
            System.out.println("cid" + cid);
            for(int b=0;b<i;b++){
                select = crosskservice.query(cid);
                System.out.println("select:" + select);
                if(select.contains("content")){
                    break;
                }
                else if(select.contains("运算错误")){
                    return cid+"运算错误"+select;
                }
                Thread.sleep(10000);
            }

            int before = select.indexOf("content");
            int after = select.indexOf("\"}]}");
            acceptRequest += select.substring(before + 10, after);
        }
        System.out.println("acceptRequest:" + acceptRequest);

        acceptRequest = "[" + acceptRequest + "]";
        String[][] arr = JSON.parseObject(acceptRequest, String[][].class);
        Double[][] ds = new Double[arr.length][arr[0].length];
        for (int m = 0; m < arr.length; m++) {
            for (int n = 0; n < arr[0].length; n++) {
                ds[m][n] = Double.valueOf(arr[m][n]);
            }
        }

        acceptRequest = "";

        Double[][] dataprocess = DataProcessing.afterprocess(ds);

        for (int q = 0; q <dataprocess.length; q++){
            acceptRequest+=Arrays.toString(dataprocess[q])+",";
        }
        acceptRequest="["+acceptRequest.substring(0, acceptRequest.length()-1)+"]";

        System.out.println("output:" + acceptRequest);
        return acceptRequest;
    }


}
