package com.liwei.redisstudy.service.impl;

import com.liwei.redisstudy.constant.RedisConstant;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.StudentRankVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
@Slf4j
@Service
public class RankServiceImpl implements IRankService {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean initRank() {
        //todo 内存初始化学生分数
        return true;
    }

    @Override
    public boolean executeRank() {
        //初始化
        init();
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
                StudentRankVO studentRankVO = getSchoolLastRank(schoolId);
                if (studentRankVO.getRank() < personNum) {
                    //入围
                    redisService.zAdd(RedisKeyBuilder.getKeyZsetSchoolRank(schoolId), userId, totalScore);
                    redisService.hmSet(studentKey, schoolId, true);
                    break;
                }
            }
        }
        return true;
    }

    /**
     * 初始化，清理学生入围信息和学校已有排名
     *
     * @param
     * @return
     */
    private void init() {
        String pattern = RedisKeyBuilder.getKeyHashStudent("*");
        Set<String> sets = redisService.keys(pattern);
        for (String studentKey : sets) {
            Set<Object> hashKeys = redisService.hmHashKeys(studentKey);
            for (Object object : hashKeys) {
                String schoolId = (String) object;
                //todo 批量更新
                redisService.hmSet(studentKey, schoolId, false);
            }
        }

        String schoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank("*");
        Set<String> schoolRankSets = redisService.keys(schoolRankPattern);
        for (String key : schoolRankSets) {
            //TODO 批量删除
            redisService.remove(key);
        }
    }

    @Override
    public StudentRankVO getSchoolLastRank(String schoolId) {
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(schoolId);
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(schoolRankKey);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        return ranking(result, rankMap);
    }

    @Override
    public Integer mySchoolRank(String schoolId, String userId) {
        Integer rank = -1;
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(schoolId);
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(schoolRankKey);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        ranking(result, rankMap);
        for (Object item : rankMap.keySet()) {
            if (Objects.equals(item, userId)) {
                rank = rankMap.get(item);
                break;
            }
        }
        return rank;
    }

    @Override
    public List<String> schoolStudentList(String schoolId) {
        List list = new ArrayList();
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(schoolId);
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(schoolRankKey);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String value = String.valueOf(typedTuple.getValue());
            double currentScore = typedTuple.getScore();
            list.add(value);
        }
        return list;
    }

    @Override
    public void studentSubmitWill(String userId, List<String> schoolList) {
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(userId);
        for (String schoolId : schoolList) {
            redisService.hmSet(keyHashStudent, schoolId, false);
        }
    }

    /**
     * 非连续排名
     *
     * @param result  有序集合
     * @param rankMap 排名集合
     * @return
     */
    private static StudentRankVO ranking(Set<ZSetOperations.TypedTuple<Object>> result, Map<Object, Integer> rankMap) {
        double lastScore = -1.0;
        Integer rank = 0;
        String userId = null;
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            userId = String.valueOf(typedTuple.getValue());
            double currentScore = typedTuple.getScore();
            log.debug(userId + "->" + currentScore);
            if (!Objects.equals(lastScore, currentScore)) {
                rankMap.put(userId, ++rank);
            } else {
                //并列
                rankMap.put(userId, rank++);
            }
            lastScore = typedTuple.getScore();
        }
        return new StudentRankVO(userId, rank);
    }

}
