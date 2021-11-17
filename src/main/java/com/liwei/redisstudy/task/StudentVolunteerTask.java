package com.liwei.redisstudy.task;

import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.RedisService;

import java.util.Map;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/17
 */
public class StudentVolunteerTask extends Thread {

    private RedisService redisService;

    private String examId;

    private String userId;

    private Map<Object, Object> map;

    public StudentVolunteerTask(RedisService redisService, String examId, String userId, Map<Object, Object> map) {
        this.redisService = redisService;
        this.examId = examId;
        this.userId = userId;
        this.map = map;
    }

    @Override
    public void run() {
        redisService.hmBatchSet(RedisKeyBuilder.getKeyHashStudent(examId, userId), map);
    }
}
