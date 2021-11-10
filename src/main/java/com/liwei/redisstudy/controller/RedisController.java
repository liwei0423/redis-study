package com.liwei.redisstudy.controller;

import com.liwei.redisstudy.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/10
 */
@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @RequestMapping("set")
    public String set(String key, String value) {
        redisService.set(key, value);
        return "success";
    }
}
