package com.liwei.redisstudy;

import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.task.StudentVolunteerTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void studentVolunteer() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String pattern = RedisKeyBuilder.getKeyHashStudent(examId, "*");
        Set<String> sets = redisService.keys(pattern);
        redisService.removeBatch(sets);

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < studentNum; i++) {
            String userId = String.valueOf(i + 1);
            List<String> willList = listRandom(schoolList, studentWillNum);
            Map<Object, Object> map = new LinkedHashMap<>();
            for (String schoolId : willList) {
                map.put(schoolId, false);
            }
            executorService.execute(new StudentVolunteerTask(redisService, examId, userId, map));
//            redisService.hmBatchSet(RedisKeyBuilder.getKeyHashStudent(examId, userId), map);
        }
        executorService.awaitTermination(500, TimeUnit.SECONDS);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    private static List<String> listRandom(List<String> list, Integer num) {
        Collections.shuffle(list);
        return list.subList(0, num);
    }


}

