package com.liwei.redisstudy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liwei.redisstudy.vo.StudentWillVO;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/17
 */
public class JavaTest {

    @Test
    public void testRandom() {
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            int ran1 = r.nextInt(10);
            System.out.println(ran1);
        }
    }

    @Test
    public void testJson() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for(int i=0;i<100000;i++){
            List<StudentWillVO> list = new ArrayList<>();
            list.add(new StudentWillVO("11", false));
            list.add(new StudentWillVO("22", false));
            String jsonString = JSONObject.toJSONString(list);
//            System.out.println(jsonString);
            List<StudentWillVO> list2 = JSONArray.parseArray(jsonString, StudentWillVO.class);
            for (StudentWillVO item : list2) {
//                System.out.println(item);
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}
