package com.moranstart.jugo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.moranstart.jugo.algorithm.moran103;
import com.juzix.jugo.circuit.InputCallback;
import com.juzix.jugo.circuit.OutputCallback;
import com.juzix.jugo.circuit.datatypes.*;
import com.moranstart.jugo.conf.Const;
import com.moranstart.jugo.dto.ResultDto;
import com.moranstart.jugo.entity.Input;
import com.moranstart.jugo.entity.Result;
import com.moranstart.jugo.mapper.InputMapper;
import com.moranstart.jugo.mapper.ResultMapper;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.node.exception.MPCException;
import com.moranstart.jugo.service.MoranStartService;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class MoranStartServiceImpl implements MoranStartService {

    private static Logger logger = LoggerFactory.getLogger(MoranStartServiceImpl.class);

    private static moran103 moran;

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
    
    @Value("${nodeEndpoint}")
    private String nodeEndpoint;

    @Value("${circuitsFile}")
    private String circuitsFile;

    @Override
    public String startCompareTask(String id, String input, Integer type, String roomId, String user1, String user2) {
    	//查询id是否重复
    	Input inputObject = inputMapper.seletByCond(id);
    	if(inputObject != null){
    		ResultDto resultDto = new ResultDto(2, "id重复请重复输入");
            return JSONObject.toJSONString(resultDto);
    	}
    	String[] argsAttach = new String[]{id, input}; //,"--Mpc.Circuits="+circuitsFile
        String room = "";
        String userName1 = "";
        String userName2 = "";
        //可以根据roomId来区分是否由外部输入用户还是常量用户
        if(StringUtils.isNotBlank(roomId)){
        	room = roomId;
        	userName1 = user1;
        	userName2 = user2;
        } else {
        	room = localRoomId;
        	userName1 = node1Name;
        	userName2 = node2Name;
        }
        
        // 初始化节点连接信息，连接成功后会进行callback回调函数的设置
        if (null == moran ) {
        	//初始化节点对象
            moran = new moran103(cirName, userName1,nodePassword, NodeCommunicateMode.CALLBACK,
                    nodeEndpoint, null, argsAttach);

            //设置回调callback逻辑
            moran.setOutputCallbackForORG(new OutputCallback<Int64>(Int64.class) {
	            @Override
	            public void onResult(String taskId, String algorithmId, int resultCode, Int64 result, String[] args, MPCTaskType mpcTaskType) {
	                logger.debug("获取到结果：任务ID：{},算法ID：{},错误码：{}，值：{}",
	                        taskId, algorithmId, resultCode, result.getValue().toString());
	                //根据结果进行判断，设置结果进数据库中
                    System.out.println(args[0]+"获取最终结果"+result.getValue().intValue());

                    resultMapper.insertData(args[0], "结果:"+result.getValue().intValue(), new Date(),Const.getType_three());
	            }
	
	            @Override
	            public void onFailure(Throwable e, String[] args) {
	            	//失败设置原因
	                e.printStackTrace();
	                resultMapper.insertData(args[0], "运算错误，原因 :" + e.getMessage() ,new Date(), Const.getType_one());
	            }
	        });
	
            // 邀请方定义输入参数
            moran.setInputCallbackForORG(new InputCallback<Int64[]>() {
                @Override
                public Int64[] onInput(String taskId, String algorithmId, String[] args) {

                	//发起方直接从数组中获取输入数据
                    resultMapper.insertData(args[0], "准备参与计算参数，发起计算", new Date(),Const.getType_one());

                    String[] strarr=args[1].split(",");
                    Int64[] dimesionTpl = new Int64[13];

                    for(int i=0; i<strarr.length; i++){
                        dimesionTpl[i] =new Int64(BigInteger.valueOf(Long.parseLong(strarr[i])));
                    }

                    System.out.println("发起计算zhi"+dimesionTpl);
                    return dimesionTpl;
                    //new Uint32(BigInteger.valueOf(Long.parseLong(args[1])));
                }

                @Override
                public void onFailure(Throwable e,String[] args) {
                	//失败设置原因
                    e.printStackTrace();
                    resultMapper.insertData(args[0], "运算错误，原因 :" + e.getMessage() ,new Date(), Const.getType_one());
                }
            });
        }

        try {
        	//参与者列表，顺序：发起者，被邀请者
	        List<String> takerList = Arrays.asList(new String[]{userName1, userName2});
	        //接收者列表，可以定义谁进行接收结果
	        List<String> resulReceiverList = Arrays.asList(new String[]{userName1, userName2});
	        //开启任务，argsAttach数组数据将会同步到被邀请方中。
            moran.doCompute(room, argsAttach, takerList, resulReceiverList);
        } catch (MPCException e) {
            e.printStackTrace();
            ResultDto resultDto = new ResultDto(3, "发起失败，请重新计算");
            return JSONObject.toJSONString(resultDto);
        }
        logger.info("当前节点为发起方，即将开启计算.");
        inputMapper.insertData(id, input);
        resultMapper.insertData(id, "当前节点为发起方，即将开启计算.",new Date(), Const.getType_one());
        ResultDto resultDto = new ResultDto(0, "成功");
        return JSONObject.toJSONString(resultDto);
    }

    @Override
    public String query(String id) {
        //初始化返回数据
        ResultDto resultDto = new ResultDto(0, "成功");
        System.out.println("当前开启计算");
        int count = resultMapper.seletCountByCond(id);
        System.out.println("result count:"+count);
        if (count < 2) {
            resultDto.setRet(1);
            resultDto.setMessage("计算错误，请重新计算");
            return JSONObject.toJSONString(resultDto);
        }
        //获取发起方列表
        //List<Result> results = resultMapper.seletByCond(id, Const.getType_one());
        //resultDto.setStartData(results);
        //获取接收方列表
        List<Result> resultsList = resultMapper.seletByCond(id, Const.getType_three());
        resultDto.setAcceptData(resultsList);
        return JSONObject.toJSONString(resultDto);
    }

}
