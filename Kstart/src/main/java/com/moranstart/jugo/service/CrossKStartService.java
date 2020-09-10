package com.moranstart.jugo.service;

public interface CrossKStartService {

    /**
     * 接收算法
     *
     * @param input
     * @return
     */
    String startCompareTask(String id, String input, Integer type, String roomId, String user1, String user2);

    /**
     * 查询算法结果
     *
     * @param id
     * @return
     */
    String query(String id);
}
