package com.localmoran.example.demo.service;

public interface LocalMoranStarService {

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
