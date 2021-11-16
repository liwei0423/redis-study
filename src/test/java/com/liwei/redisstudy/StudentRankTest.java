package com.liwei.redisstudy;

import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

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
        if (redisService.exists(studentScoreKey)) {
            redisService.remove(studentScoreKey);
        }
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
        String pattern = RedisKeyBuilder.getKeyHashStudent(examId, "*");
        Set<String> sets = redisService.keys(pattern);
        for (String key : sets) {
            //TODO 批量删除
            redisService.remove(key);
        }

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "1"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "1"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "2"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "2"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "3"), "33", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "3"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "4"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "4"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "5"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "5"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "6"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "6"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "7"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "7"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "8"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "8"), "11", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "9"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId, "9"), "33", false);
    }

    @Test
    public void schoolRecruit() {
        String pattern = RedisKeyBuilder.getKeyHashSchool(examId, "*");
        Set<String> sets = redisService.keys(pattern);
        for (String key : sets) {
            //TODO 批量删除
            redisService.remove(key);
        }

        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId, "11"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId, "22"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool(examId, "33"), "personNum", 3);
    }

    @Test
    public void executeRank() {
        boolean flag = rankService.executeRank(examId);
        System.out.println("return=" + flag);
    }

}
