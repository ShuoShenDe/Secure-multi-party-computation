package com.accept.jugo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.accept.jugo.algorithm.moran103;
import com.juzix.jugo.circuit.InputCallback;
import com.juzix.jugo.circuit.OutputCallback;
import com.juzix.jugo.circuit.datatypes.Int32;
import com.juzix.jugo.circuit.datatypes.Int64;
import com.juzix.jugo.circuit.datatypes.Uint32;
import com.accept.jugo.conf.Const;
import com.accept.jugo.dto.ResultDto;
import com.accept.jugo.entity.Input;
import com.accept.jugo.entity.Result;
import com.juzix.jugo.node.common.MPCTaskType;
import com.juzix.jugo.slice.common.NodeCommunicateMode;
import com.accept.jugo.mapper.InputMapper;
import com.accept.jugo.mapper.ResultMapper;
import com.accept.jugo.service.MoranAcceptService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Date;


@Service
public class TestAcceptServiceImpl implements MoranAcceptService {

    private static Logger logger = LoggerFactory.getLogger(TestAcceptServiceImpl.class);

    private static moran103 moran;

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
    public String startAccept(String id, String input,String roomId,String user) {
        //查询id是否重复
    	Input inputObject = inputMapper.seletByCond(id);
    	//String sql="INSERT INTO input VALUES ('"+input+"', "+id+");";
        //Boolean insert=InsertData.connectdatabase(sql,"morantest","postgres","webgis327");

    	if(inputObject != null){
    		ResultDto resultDto = new ResultDto(2, "id重复请重复输入");
            return JSONObject.toJSONString(resultDto);
    	}
        String[] argsAttach = new String[]{id, input}; //"--Mpc.Circuits="+circuitsFile
        
        String userName = "";
        //可以根据roomId来区分是否由外部输入用户还是常量用户
        if(StringUtils.isNotBlank(roomId)){
        	userName = user;
        } else {
        	userName = nodeName;
        }

        // 初始化节点连接信息，连接成功后会进行callback回调函数的设置
        if (null == moran ) {
            //初始化节点对象
            moran = new moran103(cirName, userName, nodePassword, NodeCommunicateMode.CALLBACK, nodeEndpoint, null, argsAttach);
            //设置回调callback逻辑
            moran.setOutputCallbackForDST(new OutputCallback<Int64>(Int64.class) {
	            @Override
	            public void onResult(String taskId, String algorithmId, int resultCode, Int64 result, String[] args, MPCTaskType mpcTaskType) {
	                logger.debug("获取到结果：任务ID：{},算法ID：{},错误码：{}，值：{}",
	                        taskId, algorithmId, resultCode, result.getValue().toString());
	              //根据结果进行判断，设置结果进数据库中
                    System.out.println("moran计算结果为："+result.getValue().intValue());
                    resultMapper.insertData(args[0], "结果："+result.getValue().toString(), Const.getType_two());
	            }
	
	            @Override
	            public void onFailure(Throwable e,String[] args) {
                    //失败设置原因
	                e.printStackTrace();
	                resultMapper.insertData(args[0], "运算错误，原因 :"+ e.getMessage() , Const.getType_one());
	            }
	        });

            // 被邀请方定义输入数据
            moran.setInputCallbackForDST(new InputCallback<Int64[]>() {
	            @Override
	            public Int64[] onInput(String taskId, String algorithmId, String[] args) {
                    //被邀请方需要通过id查询数据库，获取唯一输入数据
                    Input input = inputMapper.seletByCond(args[0]);
	            	//long byteNum = Long.parseLong(input.getInput());
	                resultMapper.insertData(args[0], "准备参与计算参数，参与计算", Const.getType_two());

	                System.out.println("计算数"+input.getInput());
                    String[] strarr=input.getInput().split(",");
                    Int64[] dimesionTpl = new Int64[13];



                    for(int i=0; i<strarr.length; i++){
                        dimesionTpl[i] =new Int64(BigInteger.valueOf(Long.parseLong(strarr[i])));
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
        //计算错误，请重新计算
        List<Result> results = resultMapper.seletByCond(id, Const.getType_one());
        resultDto.setStartData(results);
        //获取接收方列表
        List<Result> resultsList = resultMapper.seletByCond(id, Const.getType_two());
        resultDto.setAcceptData(resultsList);
        return JSONObject.toJSONString(resultDto);
    }

}
