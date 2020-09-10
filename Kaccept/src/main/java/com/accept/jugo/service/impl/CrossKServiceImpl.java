package com.accept.jugo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.juzix.jugo.circuit.InputCallback;
import com.juzix.jugo.circuit.OutputCallback;
import com.juzix.jugo.circuit.datatypes.Int32;
import com.accept.jugo.conf.Const;
import com.accept.jugo.dto.ResultDto;
import com.accept.jugo.entity.Input;
import com.accept.jugo.entity.Result;
import com.accept.jugo.algorithm.crossk10_10;

import com.juzix.jugo.circuit.datatypes.Uint32;
import com.juzix.jugo.circuit.datatypes.Uint64;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import com.accept.jugo.mapper.InputMapper;
import com.accept.jugo.mapper.ResultMapper;
import com.accept.jugo.service.CrossKAcceptService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;


@Service
public class CrossKServiceImpl implements CrossKAcceptService {

    private static Logger logger = LoggerFactory.getLogger(CrossKServiceImpl.class);

    private static crossk10_10 crossK;

    @Resource
    private InputMapper inputMapper;

    @Resource
    private ResultMapper resultMapper;

    @Value("${cirName}")
    private String cirName;


    @Value("${nodeName}")
    private String nodeName;

    @Value("${nodePassword}")
    private String nodePassword;

    @Value("${nodeEndpoint}")
    private String nodeEndpoint;

    @Value("${circuitsFile}")
    private String circuitsFile;

    @Override
    public String startAccept(String id, String input, String roomId, String user) {
        //查询id是否重复
        System.out.print(input);
        Input inputaccept = inputMapper.seletByCond(id);

        if (inputaccept != null) {
            ResultDto resultDto = new ResultDto(2, "id重复请重复输入");
            return JSONObject.toJSONString(resultDto);
        }
        String[] argsAttach = new String[]{id, input.toString(),"--Mpc.Circuits="+circuitsFile};

        String userName = "";
        //可以根据roomId来区分是否由外部输入用户还是常量用户
        if (StringUtils.isNotBlank(roomId)) {
            userName = user;
        } else {
            userName = nodeName;
        }

        // 初始化节点连接信息，连接成功后会进行callback回调函数的设置
        if (null == crossK) {

                crossK = null;

                //初始化节点对象
                crossK = new crossk10_10(cirName , userName, nodePassword, NodeCommunicateMode.CALLBACK, nodeEndpoint, null, argsAttach);
                //设置回调callback逻辑

                crossK.setOutputCallbackForDST(new OutputCallback<Uint64[]> (Uint64[].class) {
                    @Override
                    public void onResult(String taskId, String algorithmId, int resultCode, Uint64[] result, String[] args, MPCTaskType mpcTaskType) {

                        String out = "";
                        for (int i=0;i< result.length;i++) {
                            out = out + result[i].getValue().toString() + ";";
                        }
                        logger.debug("获取到结果：任务ID：{},算法ID：{},错误码：{}，值：{}",
                                taskId, algorithmId, resultCode, out);
                        //根据结果进行判断，设置结果进数据库中
                        System.out.println("crossK值：" + out);
                        resultMapper.insertData(args[0], "结果：" + out, Const.getType_two());

                    }

                    @Override
                    public void onFailure(Throwable e, String[] args) {
                        //失败设置原因
                        e.printStackTrace();
                        resultMapper.insertData(args[0], "运算错误，原因 :" + e.getMessage()+e.getCause().toString(), Const.getType_one());
                    }
                });

                // 被邀请方定义输入数据
                crossK.setInputCallbackForDST(new InputCallback<crossk10_10.Point[]>() {
                    @Override
                    public crossk10_10.Point[] onInput(String taskId, String algorithmId, String[] args) {
                        //被邀请方需要通过id查询数据库，获取唯一输入数据
                        Input input = inputMapper.seletByCond(args[0]);

                        resultMapper.insertData(args[0], "准备参与计算参数，参与计算", Const.getType_two());

                        System.out.println("输入：" + input.getInput());

                        String[] inputxandy = input.getInput().split(",");

                        crossk10_10.Point[] points = new crossk10_10.Point[inputxandy.length];

                        for (int i = 0; i < inputxandy.length; i++) {
                            String[] splitxy = inputxandy[i].split("-");
                            String sx = splitxy[0];
                            String sy = splitxy[1];
                            System.out.println(i + "输入计算数x" + sx + "y+" + sy);

                            crossk10_10.Point point = new crossk10_10.Point();
                            Uint64 x = new Uint64(BigInteger.valueOf(Long.parseLong(sx)));
                            Uint64 y = new Uint64(BigInteger.valueOf(Long.parseLong(sy)));
                            point.setX(x);
                            point.setY(y);
                            points[i] = point;
                            System.out.println("inputx:" + point.getX().getValue());
                            System.out.println("inputy:" + point.getY().getValue());
                        }

                        System.out.println("inputxandy" + inputxandy.length + "points：" + points.toString());
                        return points;
                        //return new Uint32(BigInteger.valueOf(byteNum));
                    }

                    @Override
                    public void onFailure(Throwable e, String[] args) {
                        //失败设置原因
                        e.printStackTrace();
                        resultMapper.insertData(id, "get wrong  运算错误，原因 :" + e.getMessage(), Const.getType_two());
                    }

                });

        }

        logger.info("当前启动节点为被邀请方，不开启计算.");
        inputMapper.insertData(id, input);
        resultMapper.insertData(id, "当前启动节点为被邀请方，不开启计算.等待计算发起", Const.getType_two());
        ResultDto resultDto = new ResultDto(0, "成功");
        return JSONObject.toJSONString(resultDto);
    }


    @Override
    public String query(String id) {
        //初始化返回数据
        ResultDto resultDto = new ResultDto(0, "成功");
        int count = resultMapper.seletCountByCond(id);
        if (count < 2) {
            resultDto.setRet(1);
            resultDto.setMessage("calculate is wrong, please again. 计算错误，请重新计算");
            return JSONObject.toJSONString(resultDto);
        }
        //获取发起方列表
        List<Result> results = resultMapper.seletByCond(id, Const.getType_one());
        resultDto.setStartData(results);
        //获取接收方列表
        List<Result> resultsList = resultMapper.seletByCond(id, Const.getType_two());
        resultDto.setAcceptData(resultsList);
        return JSONObject.toJSONString(resultDto);
    }

}
