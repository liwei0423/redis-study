package com.liwei.redisstudy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.SchoolInfoVO;
import com.liwei.redisstudy.vo.StudentRankVO;
import com.liwei.redisstudy.vo.StudentWillVO;
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

        Map<Object, Object> map = new HashMap<>();
        for (String schoolId : schoolPersonNumList.keySet()) {
            Integer personNum = schoolPersonNumList.get(schoolId);
            map.put(schoolId, JSON.toJSONString(new SchoolInfoVO(personNum)));
        }
        String keyHashSchool = RedisKeyBuilder.getKeyHashSchool(examId);
        redisService.hmBatchSet(keyHashSchool, map);
        return true;
    }

    @Override
    public boolean clearMemory(String examId) {
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        if (redisService.exists(studentScoreKey)) {
            redisService.remove(studentScoreKey);
        }
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.remove(keyHashStudent);
        String keyHashSchool = RedisKeyBuilder.getKeyHashSchool(examId);
        redisService.remove(keyHashSchool);
        String keyZsetSchoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, "*");
        Set<String> schoolRankSets = redisService.keys(keyZsetSchoolRankPattern);
        redisService.removeBatch(schoolRankSets);
        String keyHashStudentResult = RedisKeyBuilder.getKeyHashStudentResult(examId);
        redisService.remove(keyHashStudentResult);
        return true;
    }

    @Override
    public boolean executeRank(String examId) {
        //初始化
        init(examId);
        String studentResultKey = RedisKeyBuilder.getKeyHashStudentResult(examId);
        String studentKey = RedisKeyBuilder.getKeyHashStudent(examId);
        //学生由高到底
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(RedisKeyBuilder.getKeyZsetStudentScore(examId));
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String userId = String.valueOf(typedTuple.getValue());
            double totalScore = typedTuple.getScore();
            //学生志愿信息
            String studentWillString = (String) redisService.hmGet(studentKey, userId);
            List<StudentWillVO> studentWillVOS;
            try {
                studentWillVOS = JSON.parseArray(studentWillString, StudentWillVO.class);
            } catch (Exception e) {
                System.out.println("studentWillString=" + studentWillString);
                throw e;
            }
            for (StudentWillVO studentWillVO : studentWillVOS) {
                //志愿校
                String schoolId = studentWillVO.getSchoolId();
                //学校信息key
                String keyHashSchool = RedisKeyBuilder.getKeyHashSchool(examId);
                String schoolInfoStr = (String) redisService.hmGet(keyHashSchool, schoolId);
                SchoolInfoVO schoolInfoVO = JSONObject.parseObject(schoolInfoStr, SchoolInfoVO.class);
                //获取招生人数
                Integer personNum = schoolInfoVO.getPersonNum();
                //学校投档key
                StudentRankVO studentRankVO = getSchoolLastRank(examId, schoolId);
                if (studentRankVO.getRank() < personNum) {
                    //入围
                    redisService.zAdd(RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId), userId, totalScore);
                    redisService.hmSet(studentResultKey, userId, schoolId);
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
        String keyHashStudentResult = RedisKeyBuilder.getKeyHashStudentResult(examId);
        redisService.remove(keyHashStudentResult);

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
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.hmSet(keyHashStudent, userId, JSON.toJSONString(schoolList));
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
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId), schoolId, JSON.toJSONString(new SchoolInfoVO(personNum)));
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
