package com.localmoran.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.localmoran.example.demo.database.DataProcessing;
import com.localmoran.example.demo.database.PostRequest;
import com.localmoran.example.demo.database.SelectData;
import com.localmoran.example.demo.service.LocalMoranStarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private LocalMoranStarService localmoranservice;

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

        JSONObject result = new JSONObject();
        String sql = "SELECT  value FROM " + filename + ";";
        List<Double> selectdata = SelectData.selectdatabase(sql, "postgis");

        //standardization the select data
        List<Integer> standdaata = DataProcessing.standardization(selectdata);
        double sumsqure = DataProcessing.sumsqurestandardization(selectdata);

        String moranserverreturn = "";
        int simulation = 100;
        System.out.println("sumsqure" + sumsqure);
        double[] moranResult = new double[selectdata.size()];
        int[] maxResult = new int[selectdata.size()];
        int[] minResult = new int[selectdata.size()];
        double[] pResult = new double[selectdata.size()];
        int[] zp = new int[standdaata.size()];
        String cid = "";
        String MoranI = "";
        int i = 0;
        for (; i < simulation; i++) {
            String inputdata = standdaata.toString().substring(1, standdaata.toString().length() - 1).replace(" ", "");
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }
            moranserverreturn = localmoranservice.startCompareTask(cid, inputdata, 1, "", "", "");

            Collections.shuffle(standdaata);

            if (moranserverreturn.contains("成功")) {
                continue;
            } else {
                result.put("error", moranserverreturn);
                return result.toString();
            }

        }

        Thread.sleep(10000);

        for (int j = 0; j < simulation; j++) {
            if (j == 0) {
                cid = id;
            } else {
                cid = id + j;
            }
            for (int k = 0; k < standdaata.size(); k++) {
                MoranI = localmoranservice.query(cid);
                if (MoranI.contains("content")) {
                    break;
                }
                Thread.sleep(5000);
            }
            System.out.println("MoranI:" + MoranI);
            int firstindex = MoranI.indexOf("结果:");
            int secondindex = MoranI.indexOf("]", firstindex);
            String[] sub = MoranI.substring(firstindex + 4, secondindex).split(",");
            System.out.println("selectdata" + selectdata.size());
            System.out.println("sub" + Arrays.toString(sub));
            if (j == 0) {
                for (int k = 0; k < selectdata.size(); k++) {
                    //sum +=Integer.parseInt(sub[k]);
                    moranResult[k] = (selectdata.size() - 1) * 0.01 * 0.01 * 0.01* Integer.parseInt(sub[k]) / sumsqure;
                }
                System.out.println("moranResult 结果" + " " + ":" + Arrays.toString(moranResult));

            } else {
                for (int k = 0; k < selectdata.size(); k++) {
                    //sum +=Integer.parseInt(sub[k]);
                    double morani = (selectdata.size() - 1) * 0.01 * 0.01 * 0.01 * Integer.parseInt(sub[k]) / sumsqure;
                    if (morani < moranResult[k]) {
                        minResult[k] += 1;
                    } else {
                        maxResult[k] += 1;
                    }

                }
            }

        }
        System.out.println("moranResult 结果" + " " + ":" + Arrays.toString(moranResult));
        System.out.println("minResult 结果" + " " + ":" + Arrays.toString(minResult));
        System.out.println("maxResult 结果" + " " + ":" + Arrays.toString(maxResult));
        for (int p = 0; p < selectdata.size(); p++) {
            if (maxResult[p] < minResult[p]) {
                pResult[p] = (maxResult[p] + 1) * 1.00 / (simulation + 1);
            } else {
                pResult[p] = (minResult[p] + 1) * 1.00 / (simulation + 1);
            }
        }
        System.out.println("pResult 结果" + " " + ":" + Arrays.toString(pResult));


        //calculate quads
        for (int m = 0; m < standdaata.size(); m++) {
            if (standdaata.get(m) > 0) {
                zp[m] = 1;
            } else {
                zp[m] = 0;
            }
        }

        String zl = PostRequest.request("http://202.114.114.46:8082/localcross/quadsResult?filename=light", "");
        String[] lp = zl.substring(1, zl.length() - 1).split(",");
        double[] quads = new double[standdaata.size()];
        for (int z = 0; z < standdaata.size(); z++) {
            if (pResult[z] > 0.1) {
                quads[z] = 0;
                continue;
            }
            double pp = zp[z] * Double.valueOf(lp[z]);
            double np = (1 - zp[z]) * Double.valueOf(lp[z]);
            double nn = (1 - zp[z]) * (1 - Double.valueOf(lp[z]));
            double pn = zp[z] * (1 - Double.valueOf(lp[z]));
            quads[z] = 1 * pp + 2 * np + 3 * nn + 4 * pn;
        }
        System.out.println("quads 结果" + ":" + Arrays.toString(quads));

        return "[" + Arrays.toString(moranResult) + "," + Arrays.toString(pResult) + "," + Arrays.toString(quads) + "]";
    }


}
