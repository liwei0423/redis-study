package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.StudentWillVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
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
        String keyHashStudentInfo = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        if (redisService.exists(keyHashStudentInfo)) {
            redisService.remove(keyHashStudentInfo);
        }
        Map<String, Double> map = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < studentNum; i++) {
            String userId = String.valueOf(i + 1);
            double score = random.nextInt(100);
            map.put(userId, score);
        }
        redisService.zBatchAdd(keyHashStudentInfo, map);
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
            map.put(userId, studentWillJsonString(willList));
        }
        redisService.hmBatchSet(keyHashStudent, map);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    private static String studentWillJsonString(List<String> schoolIds) {
        List<StudentWillVO> list = new ArrayList<>();
        for (String schoolId : schoolIds) {
            list.add(StudentWillVO.builder().wishId("101").schoolId(schoolId).type("1").build());
        }
        return JSON.toJSONString(list);
    }

    @Test
    public void schoolRecruit() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String keyHashSchoolRecruit = RedisKeyBuilder.getKeyHashSchoolRecruit(examId);
        redisService.remove(keyHashSchoolRecruit);
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < schoolNum; i++) {
            String schoolId = String.valueOf(i + 1);
//            map.put(schoolId, JSON.toJSONString(new SchoolInfoVO(schoolStudentNum)));
        }
        redisService.hmBatchSet(keyHashSchoolRecruit, map);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    @Test
    public void executeRank() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        studentScore();
//        studentVolunteer();
        schoolRecruit();
        boolean flag = rankService.executeRank(examId);
        System.out.println("return=" + flag);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    @Test
    public void testList() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RedisTemplate redisTemplate = redisService.getRedisTemplate();
        redisTemplate.execute((RedisCallback) connection -> {
            connection.openPipeline();
            for (int i = 0; i < 100000; i++) {
                connection.lPush("111".getBytes(), String.valueOf(i + 1).getBytes());
            }
            return null;
        });
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    @Test
    public void clear() {
        rankService.clearMemory(examId);
    }

    @Test
    public void testQueryKey() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String studentKey = RedisKeyBuilder.getKeyHashStudent(examId);
        for (int i = 0; i < 10000; i++) {
            try {
                Object studentWillString = redisService.hmGet(studentKey, (i + 1));
            } catch (Exception e) {
                System.out.println("###=" + (i + 1));
                e.printStackTrace();
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

}

