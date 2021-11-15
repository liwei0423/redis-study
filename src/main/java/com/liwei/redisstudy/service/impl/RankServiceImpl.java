package com.liwei.redisstudy.service.impl;

import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
@Service
public class RankServiceImpl implements IRankService {
    @Autowired
    private RedisService redisService;

    @Override
    public Integer getLastRank(String key) {
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(key);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        return ranking(result, rankMap);
    }

    /**
     * 实时排名
     *
     * @param result  有序集合
     * @param rankMap 排名集合
     * @return
     */
    public static Integer ranking(Set<ZSetOperations.TypedTuple<Object>> result, Map<Object, Integer> rankMap) {
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
        return rank;
    }

}
