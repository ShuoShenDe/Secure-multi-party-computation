package com.localmoran.example.demo.Controller;

import com.localmoran.example.demo.database.DataProcessing;
import com.localmoran.example.demo.database.SelectData;
import com.localmoran.example.demo.service.LocalMoranService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping(value = "/localcross/")
@Api(value = "Sample", description = "Server for quads", produces = MediaType.ALL_VALUE)
@RestController
//@RequestMapping(value = "/accept/")
public class quadscontroller {


    @ResponseBody
    @PostMapping(value = "/quadsResult")
    @ApiOperation(value = "测试-查询结果方法", httpMethod = "POST", notes = "查询计算结果")
    public double[] quadsResult(
            //@ApiParam(required = true, name = "id", value = "唯一标识") @RequestParam(name = "id", required = true) String id,
            @ApiParam(required = true, name = "filename", value = "filename") @RequestParam(name = "filename", required = true) String filename)
            throws Exception {
        int weight[][] = {
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1},
                {0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0},
                {0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1},
                {0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0},
                {0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0}
        };

        int r[] = {3, 4, 4, 5, 4, 7, 3, 3, 3, 2, 3, 4, 5};

        String sql = "SELECT value FROM " + filename + ";";
        List<Double> selectdata = SelectData.selectdatabase(sql, "postgis");
        List<Integer> standdaata = DataProcessing.standardization(selectdata);

        double[] quads = new double[13];

        for (int i = 0; i < 13; i++) {
            double sum = 0;
            for (int j = 0; j < 13; j++) {
                sum = sum + weight[i][j] * standdaata.get(j);
            }
            if (sum > 0) {
                quads[i] = 1;
            } else {
                quads[i] = 0;
            }
        }
        System.out.println(Arrays.toString(quads));
        //return "鏌ヨ鎴愬姛";
        return quads;
    }

}
