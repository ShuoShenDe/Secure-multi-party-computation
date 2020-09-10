package com.moranstart.jugo.dto;

import com.moranstart.jugo.entity.Result;

import java.util.List;

public class ResultDto {
	
	/**
	 * 错误码
	 */
	private Integer ret;
	
	/**
	 * 错误信息
	 */
	private String message;
	
	/**
	 * 启动方日志
	 */
	private List<Result> startData;
	
	/**
	 * 被邀请方日志
	 */
	private List<Result> acceptData;
	
	public ResultDto(Integer ret, String message){
		this.ret = ret;
		this.message = message;
	}
	
	public Integer getRet() {
		return ret;
	}

	public void setRet(Integer ret) {
		this.ret = ret;
	}

	public List<Result> getStartData() {
		return startData;
	}

	public void setStartData(List<Result> startData) {
		this.startData = startData;
	}

	public List<Result> getAcceptData() {
		return acceptData;
	}

	public void setAcceptData(List<Result> acceptData) {
		this.acceptData = acceptData;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
