package com.liwei.redisstudy.controller;

import com.liwei.redisstudy.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/10
 */
@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("set")
    public String set(String key, String value) {
        redisService.set(key, value);
        return "success";
    }

    @RequestMapping("zset")
    public String set(String key, String member, double score) {
        redisService.zAdd(key, member, score);
        return "success";
    }

    @RequestMapping("ranklist")
    public String ranklist(String key) {
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(key);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        changeRank(result, rankMap);
        System.out.println("--------");
        StringBuffer sb = new StringBuffer();
        for (Object item : rankMap.keySet()) {
            System.out.println(item + "->" + rankMap.get(item));
            sb.append(item + "->" + rankMap.get(item) + "\r\n");
        }
        return sb.toString();
    }

    @RequestMapping("rank")
    public String rank(String key, String member) {
        Integer rank = -1;
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(key);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        changeRank(result, rankMap);
        System.out.println("--------");
        StringBuffer sb = new StringBuffer();
        for (Object item : rankMap.keySet()) {
            if (Objects.equals(item, member)) {
                rank = rankMap.get(item);
                break;
            }
        }
        return rank.toString();
    }


    public Map<Object, Integer> changeRank(Set<ZSetOperations.TypedTuple<Object>> result, Map<Object, Integer> rankMap) {

        double lastScore = -1.0;
        Integer rank = 0;

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String value = String.valueOf(typedTuple.getValue());
            double currentScore = typedTuple.getScore();
            System.out.println(value + "->" + currentScore);
            if (!Objects.equals(lastScore, currentScore)) {
                rankMap.put(value, ++rank);
            } else {
                //并列
                rankMap.put(value, rank++);
            }
            lastScore = typedTuple.getScore();
        }

        return rankMap;
    }
}
