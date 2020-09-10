package com.accept.jugo.service;


public interface CrossKAcceptService {

    /**
     * 启动被邀请方
     *
     * @param input
     * @return
     */
    String startAccept(String id, String input, String roomId, String user);

    /**
     * 查询算法结果
     *
     * @param id
     * @return
     */
    String query(String id);
}
