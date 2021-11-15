package com.liwei.redisstudy.service;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
public interface IRankService {

    /**
     *  获取最后一名的名次
     *
     * @param key
     * @return
     */
    Integer getLastRank(String key);
}
