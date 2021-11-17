package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.SchoolInfoVO;
import com.liwei.redisstudy.vo.StudentWillVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/12
 */
@SpringBootTest
public class StudentRankTest {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IRankService rankService;

    private final String examId = "1111";

    @Test
    public void studentScore() {

        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        redisService.remove(studentScoreKey);
        redisService.zAdd(studentScoreKey, "1", 80f);
        redisService.zAdd(studentScoreKey, "2", 82f);
        redisService.zAdd(studentScoreKey, "3", 81f);
        redisService.zAdd(studentScoreKey, "4", 86f);
        redisService.zAdd(studentScoreKey, "5", 88f);
        redisService.zAdd(studentScoreKey, "6", 72f);
        redisService.zAdd(studentScoreKey, "7", 75f);
        redisService.zAdd(studentScoreKey, "8", 60f);
        redisService.zAdd(studentScoreKey, "9", 95f);
    }

    @Test
    public void studentVolunteer() {
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.remove(keyHashStudent);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "1", studentWillJsonString("11", "22"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "2", studentWillJsonString("22", "33"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "3", studentWillJsonString("33", "22"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "4", studentWillJsonString("11", "22"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "5", studentWillJsonString("11", "33"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "6", studentWillJsonString("22", "33"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "7", studentWillJsonString("22", "33"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "8", studentWillJsonString("22", "11"));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "9", studentWillJsonString("22", "33"));
    }

    private static String studentWillJsonString(String... schoolIds) {
        List<StudentWillVO> list = new ArrayList<>();
        for (String schoolId : schoolIds) {
            list.add(new StudentWillVO(schoolId, false));
        }
        return JSON.toJSONString(list);
    }

    @Test
    public void schoolRecruit() {
        String keyHashSchool = RedisKeyBuilder.getKeyHashSchool(examId);
        redisService.remove(keyHashSchool);

        System.out.println(new SchoolInfoVO(3));

        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId), "11", JSON.toJSONString(new SchoolInfoVO(3)));
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId), "22", JSON.toJSONString(new SchoolInfoVO(3)));
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId), "33", JSON.toJSONString(new SchoolInfoVO(3)));
    }

    @Test
    public void testZsetBatchAdd() {
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        Map<String, Double> map = new HashMap<>();
        map.put("1", 80.0);
        map.put("2", 81.0);
        redisService.zBatchAdd(studentScoreKey, map);
    }

    @Test
    public void testRemoveKeyBatch() {
        Set<String> keyList = new HashSet<>();
        keyList.add("key1");
        keyList.add("key2");
        redisService.removeBatch(keyList);
    }

    @Test
    public void clear() {
        rankService.clearMemory(examId);
    }

    @Test
    public void executeRank() {
        boolean flag = rankService.executeRank(examId);
        System.out.println("return=" + flag);
    }

}
