package com.liwei.redisstudy;

import com.liwei.redisstudy.constant.RedisConstant;
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

    @Test
    public void studentScore() {
        String studentScoreKey = RedisConstant.KEY_ZSET_STUDENT_SCORE;
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
        String pattern = RedisKeyBuilder.getKeyHashStudent("*");
        Set<String> sets = redisService.keys(pattern);
        for(String key:sets){
            //TODO 批量删除
            redisService.remove(key);
        }

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("1"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("1"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("2"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("2"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("3"), "33", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("3"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("4"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("4"), "22", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("5"), "11", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("5"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("6"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("6"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("7"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("7"), "33", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("8"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("8"), "11", false);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("9"), "22", false);
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent("9"), "33", false);
    }

    @Test
    public void schoolRecruit() {
        String pattern = RedisKeyBuilder.getKeyHashSchool("*");
        Set<String> sets = redisService.keys(pattern);
        for(String key:sets){
            //TODO 批量删除
            redisService.remove(key);
        }

        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("11"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("22"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("33"), "personNum", 3);
    }

    @Test
    public void executeRank() {
        boolean flag = rankService.executeRank();
        System.out.println("return=" + flag);
    }

}
