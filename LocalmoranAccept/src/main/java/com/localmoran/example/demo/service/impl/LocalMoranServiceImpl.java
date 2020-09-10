package com.localmoran.example.demo.service.impl;

import com.juzix.jugo.circuit.datatypes.Int32;
import com.localmoran.example.demo.algorithm.localmoran13;
import com.localmoran.example.demo.conf.Const;
import com.localmoran.example.demo.dto.ResultDto;
import com.localmoran.example.demo.entity.Input;
import com.localmoran.example.demo.entity.Result;
import com.localmoran.example.demo.mapper.InputMapper;
import com.localmoran.example.demo.mapper.ResultMapper;
import com.localmoran.example.demo.service.LocalMoranService;
import com.alibaba.fastjson.JSONObject;
import com.juzix.jugo.circuit.InputCallback;
import com.juzix.jugo.circuit.OutputCallback;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;


@Service
public class LocalMoranServiceImpl implements LocalMoranService {

    private static Logger logger = LoggerFactory.getLogger(LocalMoranServiceImpl.class);

    private static localmoran13 localmoran;

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
        String[] argsAttach = new String[]{id, input,"--Mpc.Circuits="+circuitsFile};  //"

        String userName = "";
        //可以根据roomId来区分是否由外部输入用户还是常量用户
        if (StringUtils.isNotBlank(roomId)) {
            userName = user;
        } else {
            userName = nodeName;
        }

        // 初始化节点连接信息，连接成功后会进行callback回调函数的设置
        if (null == localmoran) {
            //初始化节点对象
            localmoran = new localmoran13(cirName, userName, nodePassword, NodeCommunicateMode.CALLBACK, nodeEndpoint, null, argsAttach);
            //设置回调callback逻辑

            localmoran.setOutputCallbackForDST(new OutputCallback<Int32[]>(Int32[].class) {
                @Override
                public void onResult(String taskId, String algorithmId, int resultCode, Int32[] result, String[] args, MPCTaskType mpcTaskType) {
                    String out="[";
                    int i=0;
                    for(;i<result.length-1;i++){
                        out+=result[i].getValue().intValue()+",";
                    }
                    out+=result[i].getValue().intValue()+"]";

                    logger.debug("获取到结果：任务ID：{},算法ID：{},错误码：{}，值：{}",
                            taskId, algorithmId, resultCode, out);
                    //根据结果进行判断，设置结果进数据库中
                    System.out.println("moran计算结果为："+out);
                    resultMapper.insertData(args[0], "结果："+out, Const.getType_two());
                }

                @Override
                public void onFailure(Throwable e,String[] args) {
                    //失败设置原因
                    e.printStackTrace();
                    resultMapper.insertData(args[0], "运算错误，原因 :"+ e.getMessage() , Const.getType_one());
                }
            });

            // 被邀请方定义输入数据
            localmoran.setInputCallbackForDST(new InputCallback<Int32[]>() {
                @Override
                public Int32[] onInput(String taskId, String algorithmId, String[] args) {
                    //被邀请方需要通过id查询数据库，获取唯一输入数据
                    Input input = inputMapper.seletByCond(args[0]);
                    //long byteNum = Long.parseLong(input.getInput());
                    resultMapper.insertData(args[0], "准备参与计算参数，参与计算", Const.getType_two());

                    System.out.println("计算数"+input.getInput());
                    String[] strarr=input.getInput().split(",");
                    Int32[] dimesionTpl = new Int32[strarr.length];

                    for(int i=0; i<strarr.length; i++){
                        dimesionTpl[i] =new Int32(BigInteger.valueOf(Long.parseLong(strarr[i])));
                    }

                    return dimesionTpl;
                    //return new Uint32(BigInteger.valueOf(byteNum));
                }

                @Override
                public void onFailure(Throwable e,String[] args) {
                    //失败设置原因
                    e.printStackTrace();
                    resultMapper.insertData(id, "get wrong  运算错误，原因 :"+ e.getMessage(), Const.getType_two());
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
