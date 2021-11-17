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
    public boolean initMemory(String examId, Map<String, Double> studentScoreList, Map<String, Integer> schoolPersonNumList) {
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        redisService.zBatchAdd(studentScoreKey, studentScoreList);
        for (String schoolId : schoolPersonNumList.keySet()) {
            Integer personNum = schoolPersonNumList.get(schoolId);
            redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId, schoolId), RedisConstant.PROPS_PERSON_NUM, personNum);
        }
        return true;
    }

    @Override
    public boolean clearMemory(String examId) {
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        if (redisService.exists(studentScoreKey)) {
            redisService.remove(studentScoreKey);
        }
        String keyHashStudentPattern = RedisKeyBuilder.getKeyHashStudent(examId, "*");
        Set<String> studentSets = redisService.keys(keyHashStudentPattern);
        redisService.removeBatch(studentSets);
        String keyHashSchoolPattern = RedisKeyBuilder.getKeyHashSchool(examId, "*");
        Set<String> schoolSet = redisService.keys(keyHashSchoolPattern);
        redisService.removeBatch(schoolSet);
        String keyZsetSchoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, "*");
        Set<String> schoolRankSets = redisService.keys(keyZsetSchoolRankPattern);
        redisService.removeBatch(schoolRankSets);
        return true;
    }

    @Override
    public boolean executeRank(String examId) {
        //初始化
        init(examId);
        //学生由高到底
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(RedisKeyBuilder.getKeyZsetStudentScore(examId));
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String userId = String.valueOf(typedTuple.getValue());
            double totalScore = typedTuple.getScore();
            //学生志愿信息
            String studentKey = RedisKeyBuilder.getKeyHashStudent(examId, userId);
            Set<Object> hashKeys = redisService.hmHashKeys(studentKey);
            for (Object object : hashKeys) {
                //志愿校
                String schoolId = (String) object;
                //学校信息key
                String schoolKey = RedisKeyBuilder.getKeyHashSchool(examId, schoolId);
                Object personNumObject = redisService.hmGet(schoolKey, RedisConstant.PROPS_PERSON_NUM);
                //获取招生人数
                Integer personNum = personNumObject == null ? 0 : (Integer) personNumObject;
                //学校投档key
                StudentRankVO studentRankVO = getSchoolLastRank(examId, schoolId);
                if (studentRankVO.getRank() < personNum) {
                    //入围
                    redisService.zAdd(RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId), userId, totalScore);
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
    private void init(String examId) {
        String pattern = RedisKeyBuilder.getKeyHashStudent(examId, "*");
        Set<String> sets = redisService.keys(pattern);
        for (String studentKey : sets) {
            Set<Object> hashKeys = redisService.hmHashKeys(studentKey);
            Map<Object, Object> map = new LinkedHashMap<>();
            for (Object object : hashKeys) {
                String schoolId = (String) object;
                map.put(schoolId, false);
            }
            redisService.hmBatchSet(studentKey, map);
        }

        String schoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, "*");
        Set<String> schoolRankSets = redisService.keys(schoolRankPattern);
        redisService.removeBatch(schoolRankSets);
    }

    @Override
    public StudentRankVO getSchoolLastRank(String examId, String schoolId) {
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId);
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(schoolRankKey);
        Map<Object, Integer> rankMap = new LinkedHashMap<>();
        return ranking(result, rankMap);
    }

    @Override
    public Integer mySchoolRank(String examId, String schoolId, String userId) {
        Integer rank = -1;
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId);
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
    public List<String> schoolStudentList(String examId, String schoolId) {
        List list = new ArrayList();
        String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId);
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
    public void studentWill(String examId, String userId, List<String> schoolList) {
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId, userId);
        for (String schoolId : schoolList) {
            redisService.hmSet(keyHashStudent, schoolId, false);
        }
    }

    @Override
    public void updateStudentScore(String examId, String userId, double score) {
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        Double currentScore = redisService.zGetScore(studentScoreKey, userId);
        Double increment = score - currentScore;
        redisService.incrementScore(studentScoreKey, userId, increment);
    }

    @Override
    public void updateSchoolInfo(String examId, String schoolId, Integer personNum) {
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId, schoolId), RedisConstant.PROPS_PERSON_NUM, personNum);
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
