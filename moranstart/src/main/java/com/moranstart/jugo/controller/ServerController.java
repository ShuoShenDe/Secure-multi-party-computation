package com.moranstart.jugo.controller;


import com.moranstart.jugo.database.PostRequest;
import com.moranstart.jugo.database.SelectData;
import com.alibaba.fastjson.JSONObject;

import com.moranstart.jugo.service.MoranStartService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

import com.moranstart.jugo.database.DataProcessing;

@Controller
@Api(value = "Sample", description = "Server for front end", produces = MediaType.ALL_VALUE)
@RestController
@RequestMapping(value = "/send/")
//@RequestMapping(value = "/accept/")
public class ServerController {


    @Autowired
    private MoranStartService moranService;

    @ResponseBody
    @PostMapping(value = "/sendParam")
    @ApiOperation(value = "server for website", httpMethod = "POST", notes = "accept require from website")
    public JSONObject queryResult(
            @ApiParam(required = true, name = "id", value = "id only value") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "filename", value = "filename") @RequestParam(name = "filename", required = true) String filename
    )
            throws Exception {

        JSONObject result = new JSONObject();
        String sql = "SELECT value FROM " + filename + ";";
        List<Double> selectdata = SelectData.selectdatabase(sql, "postgis");

        //standardization the select data
        List<Integer> standdaata = DataProcessing.standardization(selectdata);
        double sumsqure = DataProcessing.sumsqurestandardization(selectdata);
        double mean = 0.000;// (moranRecord[0]+moranRecord[1]+moranRecord[2])/3.00;
        double SD = 0.000;
        double sum = 0.000;
        int rmax=0;
        int rmin=0;
        double p=0;
        String moranserverreturn = "";
        List<Double> moranRecord = new ArrayList<Double>();
        System.out.println("sumsqure" + sumsqure);


        String cid = "";
        String MoranI = "";
        int i = 0;
        for (; i < 50; i++) {
            String inputdata = standdaata.toString().substring(1, standdaata.toString().length() - 1).replace(" ", "");
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }
            moranserverreturn = moranService.startCompareTask(cid, inputdata, 1, "", "", "");

            Collections.shuffle(standdaata);

            if (moranserverreturn.contains("成功")) {
                continue;
            } else {
                result.put("error", moranserverreturn);
                return result;
            }

        }

        Thread.sleep(10000);

        for (int j = 0; j < i; j++) {
            if (j != 0) {
                cid = id + j;
            } else {
                cid = id;
            }
            System.out.println("cid" + cid);

            for (int b = 0; b < i; b++) {
                MoranI = moranService.query(cid);
                if (MoranI.contains("content")) {
                    break;
                }
                Thread.sleep(2000);
            }
            int firstindex = MoranI.indexOf("结果:");
            int secondindex = MoranI.indexOf("\",\"cre_time\"", firstindex);

            Double moranResult = 0.01 * 0.01 *0.01* Integer.parseInt(MoranI.substring(firstindex + 3, secondindex))/ sumsqure;
            System.out.println("Moran 结果" + cid + " " + ":" + moranResult);
            //Record the moranResult each time
            moranRecord.add(moranResult);
        }

        SD = DataProcessing.sumsqurestandardization(moranRecord);

        for (double d : moranRecord) {
            sum += d;
            if(d>moranRecord.get(0)){
                rmax+=1;
            }else if(d<moranRecord.get(0)) {
                rmin+=1;
            }
        }
        mean = sum / selectdata.size();

        if(rmin<rmax){
            p=(rmin+1)*1.00/(i+1);
        }else {
            p=(rmax+1)*1.00/(i+1);
        }

        result.clear();
        System.out.println("SD = " + SD);
        System.out.println("sumsquare = " + sumsqure);
        System.out.println("p = " + p);
        System.out.println("moranValue = " + moranRecord.toString());
        result.put("moranValue", moranRecord.toString());
        result.put("meanValue", mean);
        result.put("SD", SD);
        result.put("p", p);

        return result;
    }


}
