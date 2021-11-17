package com.liwei.redisstudy;

import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/17
 */
@SpringBootTest
public class StressTest {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IRankService rankService;

    private final String examId = "1111";

    private final static Integer studentNum = 100000;

    private final static Integer schoolNum = 100;

    private final static Integer schoolStudentNum = 1000;

    private final static Integer studentWillNum = 5;

    private List<String> schoolList;

    static {
        for(int i=0;i<schoolNum;i++){

        }
    }

    @Test
    public void studentScore() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String studentScoreKey = RedisKeyBuilder.getKeyZsetStudentScore(examId);
        if (redisService.exists(studentScoreKey)) {
            redisService.remove(studentScoreKey);
        }
        Map<String, Double> map = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < studentNum; i++) {
            String userId = String.valueOf(i + 1);
            double score = random.nextInt(100);
            map.put(userId, score);
        }
        redisService.zBatchAdd(studentScoreKey, map);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    @Test
    public void studentVolunteer() {
        String pattern = RedisKeyBuilder.getKeyHashStudent(examId, "*");
        Set<String> sets = redisService.keys(pattern);
        redisService.removeBatch(sets);

        for (int i = 0; i < studentNum; i++) {
            String userId = String.valueOf(i + 1);

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


}

