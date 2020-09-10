package com.accept.jugo.Controller;


import com.accept.jugo.database.PointShuffler;
import com.accept.jugo.database.SelectData;
import com.accept.jugo.service.CrossKAcceptService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/send/")
@Api(value = "Sample", description = "Server for front end", produces = MediaType.ALL_VALUE)
@RestController
//@RequestMapping(value = "/accept/")
public class ServerController {

    @Autowired
    private CrossKAcceptService crossKService;

    @ResponseBody
    @PostMapping(value = "/sendParam")
    @ApiOperation(value = "server for website", httpMethod = "POST", notes = "accept require from website")
    public String queryResult(
            @ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "filename", value = "filename") @RequestParam(name = "filename", required = true) String filename
    )
            throws Exception {

        String acceptRequest = "";


        String sql = "SELECT x_coordina, y_coordina FROM " + filename + " LIMIT 10;";

        List<String> selectdata = SelectData.selectdatabase(sql, "postgis");

        System.out.println("selectdata value" + selectdata.toString());
        //standardization the select data
        String input = selectdata.toString().substring(1, selectdata.toString().length() - 1).replace(" ", "");
        int[] boundry=PointShuffler.PointBoundary(input);

        String cid = "";
        int i = 0;
        for (; i < 50; i++) {
            if (i != 0) {
                cid = id + i;
            } else {
                cid = id;
            }
            acceptRequest = crossKService.startAccept(cid, input, "", "");
            input = PointShuffler.PointShuffler(selectdata.size(),boundry);
        }
        //}
        return acceptRequest;
    }


}
