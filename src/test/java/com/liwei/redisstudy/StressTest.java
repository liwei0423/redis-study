package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
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

    private static List<String> schoolList = new ArrayList<>();

    static {
        for (int i = 0; i < schoolNum; i++) {
            schoolList.add(String.valueOf(i + 1));
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

    private static List<String> listRandom(List<String> list, Integer num) {
        Collections.shuffle(list);
        return list.subList(0, num);
    }

    @Test
    public void studentVolunteer() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.remove(keyHashStudent);

        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < studentNum; i++) {
            String userId = String.valueOf(i + 1);
            List<String> willList = listRandom(schoolList, studentWillNum);
            map.put(userId, JSON.toJSONString(willList));
        }
        redisService.hmBatchSet(keyHashStudent, map);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }


}

