package com.moranstart.jugo.service.impl;

import com.moranstart.jugo.algorithm.crossk10_10;
import com.moranstart.jugo.conf.Const;
import com.moranstart.jugo.entity.Input;
import com.alibaba.fastjson.JSONObject;
import com.juzix.jugo.circuit.InputCallback;
import com.juzix.jugo.circuit.OutputCallback;
import com.juzix.jugo.circuit.datatypes.*;
import com.moranstart.jugo.dto.ResultDto;
import com.moranstart.jugo.entity.Result;
import com.moranstart.jugo.mapper.InputMapper;
import com.moranstart.jugo.mapper.ResultMapper;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.node.exception.MPCException;
import com.moranstart.jugo.service.CrossKStartService;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.moranstart.jugo.database.GetK;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;

@Service
public class CrossKStartServiceImpl implements CrossKStartService {

    private static Logger logger = LoggerFactory.getLogger(CrossKStartServiceImpl.class);

    private static crossk10_10 crossK;

    @Resource
    private ResultMapper resultMapper;

    @Resource
    private InputMapper inputMapper;

    @Value("${cirName}")
    private String cirName;

    @Value("${localRoomId}")
    private String localRoomId;

    @Value("${node1Name}")
    private String node1Name;

    @Value("${node2Name}")
    private String node2Name;

    @Value("${nodePassword}")
    private String nodePassword;

    @Value("${circuitsFile}")
    private String circuitsFile;

    @Value("${nodeEndpoint}")
    private String nodeEndpoint;

    @Override
    public String startCompareTask(String id, String input, Integer type, String roomId, String user1, String user2) {
        //查询id是否重复
        Input inputObject = inputMapper.seletByCond(id);

        if (inputObject != null) {
            ResultDto resultDto = new ResultDto(2, "id重复请重复输入");
            return JSONObject.toJSONString(resultDto);
        }
        String[] argsAttach = new String[]{id, input,"--Mpc.Circuits="+circuitsFile};
        String room = "";
        String userName1 = "";
        String userName2 = "";
        //可以根据roomId来区分是否由外部输入用户还是常量用户
        if (StringUtils.isNotBlank(roomId)) {
            room = roomId;
            userName1 = user1;
            userName2 = user2;
        } else {
            room = localRoomId;
            userName1 = node1Name;
            userName2 = node2Name;
        }

        // 初始化节点连接信息，连接成功后会进行callback回调函数的设置
        if (null == crossK) {


            //初始化节点对象
            crossK = new crossk10_10(cirName, userName1, nodePassword, NodeCommunicateMode.CALLBACK,
                    nodeEndpoint, null, argsAttach);

            //设置回调callback逻辑
            crossK.setOutputCallbackForORG(new OutputCallback<Uint64[]>(Uint64[].class) {
                @Override
                public void onResult(String taskId, String algorithmId, int resultCode, Uint64[] result, String[] args, MPCTaskType mpcTaskType) {

                    int d=30;
                    int length=result.length;
                    long[] out=new long[length];

                    for (int i=0;i< length;i++) {
                        out[i] =  Long.parseLong(result[i].getValue().toString());
                    }
                    System.out.println("k结果" + Arrays.toString(out));
                    int[] crosskd=GetK.GetK(out,d);

                    System.out.println("k result" + Arrays.toString(crosskd));
                    logger.debug("获取到结果：任务ID：{},算法ID：{},错误码：{}，值：{}",
                            taskId, algorithmId, resultCode, out);
                    //根据结果进行判断，设置结果进数据库中
                    System.out.println("获取最终结果" + Arrays.toString(out));
                    resultMapper.insertData(args[0], Arrays.toString(crosskd)+"", Const.getType_three());
                }

                @Override
                public void onFailure(Throwable e, String[] args) {
                    //失败设置原因
                    e.printStackTrace();
                    resultMapper.insertData(args[0], "运算错误，原因 :" + e.getMessage(), Const.getType_one());
                }
            });

            // 邀请方定义输入参数
            crossK.setInputCallbackForORG(new InputCallback<crossk10_10.Point[]>() {
                @Override
                public crossk10_10.Point[] onInput(String taskId, String algorithmId, String[] args) {

                    //发起方直接从数组中获取输入数据
                    resultMapper.insertData(args[0], "准备参与计算参数，发起计算", Const.getType_one());
                    Input input = inputMapper.seletByCond(args[0]);
                    String[] inputxandy = input.getInput().split(",");
                    crossk10_10.Point[] points = new crossk10_10.Point[inputxandy.length];

                    for (int i = 0; i < inputxandy.length; i++) {
                        String[] splitxy = inputxandy[i].split("-");
                        String sx = splitxy[0];
                        String sy = splitxy[1];
                        System.out.println(i + "发起计算数x" + sx + "+,y" + sy);
                        crossk10_10.Point point = new crossk10_10.Point();
                        Uint64 x = new Uint64(BigInteger.valueOf(Long.parseLong(sx)));
                        Uint64 y = new Uint64(BigInteger.valueOf(Long.parseLong(sy)));
                        point.setX(x);
                        point.setY(y);
                        System.out.println("inputx:" + point.getX().getValue());
                        System.out.println("inputy:" + point.getY().getValue());
                        points[i] = point;
                    }

                    System.out.println("inputxandy:" + inputxandy.length + "points:" + points.length + ",args[1]" + args[1]);
                    return points;
                    //new Uint32(BigInteger.valueOf(Long.parseLong(args[1])));
                }

                @Override
                public void onFailure(Throwable e, String[] args) {
                    //失败设置原因
                    e.printStackTrace();
                    resultMapper.insertData(args[0], "运算错误，原因 :" + e.getMessage(), Const.getType_one());
                }
            });
        }

        try {
            //参与者列表，顺序：发起者，被邀请者
            List<String> takerList = Arrays.asList(new String[]{userName1, userName2});
            //接收者列表，可以定义谁进行接收结果
            List<String> resulReceiverList = Arrays.asList(new String[]{userName1, userName2});
            //开启任务，argsAttach数组数据将会同步到被邀请方中。
            crossK.doCompute(room, argsAttach, takerList, resulReceiverList);
        } catch (MPCException e) {
            e.printStackTrace();
            ResultDto resultDto = new ResultDto(3, "发起失败，请重新计算");
            return JSONObject.toJSONString(resultDto);
        }
        logger.info("当前节点为发起方，即将开启计算.");
        inputMapper.insertData(id, input);
        resultMapper.insertData(id, "当前节点为发起方，即将开启计算.", Const.getType_one());
        ResultDto resultDto = new ResultDto(0, "成功");
        return JSONObject.toJSONString(resultDto);
    }

    @Override
    public String query(String id) {
        //初始化返回数据
        ResultDto resultDto = new ResultDto(0, "成功");
        System.out.println("当前开启计算");
        int count = resultMapper.seletCountByCond(id);

        System.out.println("result count:" + count);
        if (count < 2) {
            resultDto.setRet(1);
            resultDto.setMessage("计算错误，请重新计算");
            return JSONObject.toJSONString(resultDto);
        }
        //获取发起方列表
        List<Result> results = resultMapper.seletByCond(id, Const.getType_three());
        resultDto.setStartData(results);
        //获取接收方列表
        //List<Result> resultsList = resultMapper.seletByCond(id, Const.getType_three());
        //resultDto.setAcceptData(resultsList);
        return JSONObject.toJSONString(resultDto);
    }

}
