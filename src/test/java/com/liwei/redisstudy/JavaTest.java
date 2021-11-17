package com.liwei.redisstudy;

import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/17
 */
public class JavaTest {

    @Test
    public void testRandom(){
        Random r = new Random(System.currentTimeMillis());
        for(int i=0;i<100;i++){
            int ran1 = r.nextInt(10);
            System.out.println(ran1);
        }
    }
}
