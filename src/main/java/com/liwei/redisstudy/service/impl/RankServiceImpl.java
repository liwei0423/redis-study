package com.liwei.redisstudy.service.impl;

import com.liwei.redisstudy.constant.RedisConstant;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
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
    public boolean executeRank() {
        //学生由高到底
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(RedisConstant.KEY_ZSET_STUDENT_SCORE);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String userId = String.valueOf(typedTuple.getValue());
            double totalScore = typedTuple.getScore();
            //学生志愿信息
            String studentKey = RedisKeyBuilder.getKeyHashStudent(userId);
            Set<Object> hashKeys = redisService.hmHashKeys(studentKey);
            for (Object object : hashKeys) {
                //志愿校
                String schoolId = (String) object;
                //学校信息key
                String schoolKey = RedisKeyBuilder.getKeyHashSchool(schoolId);
                Object personNumObject = redisService.hmGet(schoolKey, RedisConstant.PROPS_PERSON_NUM);
                //获取招生人数
                Integer personNum = personNumObject == null ? 0 : (Integer) personNumObject;
                //学校投档key
                String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(schoolId);
                Integer lastRank = getLastRank(schoolRankKey);
                if (lastRank < personNum) {
                    //入围
                    redisService.zAdd(schoolRankKey, userId, totalScore);
                    redisService.hmSet(studentKey, schoolId, true);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public Integer getLastRank(String key) {
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(key);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        return ranking(result, rankMap);
    }

    /**
     * 非连续排名
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
