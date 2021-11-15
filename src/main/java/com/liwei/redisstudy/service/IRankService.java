package com.liwei.redisstudy.service;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
public interface IRankService {

    /**
     *  执行入围排名，可定时30s调用一次
     *
     * @param
     * @return
     */
    boolean executeRank();

    /**
     *  获取学校最后一名的名次
     *
     * @param schoolRankKey
     * @return
     */
    Integer getLastRank(String schoolRankKey);
}
