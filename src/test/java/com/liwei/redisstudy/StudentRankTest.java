package com.liwei.redisstudy;

import com.liwei.redisstudy.constant.RedisConstant;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Iterator;
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
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "1", 80f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "2", 82f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "3", 81f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "4", 86f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "5", 88f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "6", 72f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "7", 75f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "8", 60f);
        redisService.zAdd(RedisConstant.KEY_ZSET_STUDENT_SCORE, "9", 95f);
    }

    @Test
    public void studentVolunteer() {
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
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("11"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("22"), "personNum", 3);
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchool("33"), "personNum", 3);
    }

    @Test
    public void rank() {
        //学生由高到底
        Set<ZSetOperations.TypedTuple<Object>> result = redisService.reverseRangeWithScores(RedisConstant.KEY_ZSET_STUDENT_SCORE);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String userId = String.valueOf(typedTuple.getValue());
            double totalScore = typedTuple.getScore();
            //学生志愿信息
            String studentKey = RedisKeyBuilder.getKeyHashStudent(userId);
            Set<Object> hashKeys = redisService.hmHashKeys(studentKey);
            for (Object object : hashKeys) {
                //志愿校
                String schoolId = (String) object;
                //学校信息key
                String schoolKey = RedisKeyBuilder.getKeyHashSchool(schoolId);
                Object personNumObject = redisService.hmGet(schoolKey, RedisConstant.PROPS_PERSON_NUM);
                //获取招生人数
                Integer personNum = personNumObject == null ? 0 : (Integer) personNumObject;
                //学校投档key
                String schoolRankKey = RedisKeyBuilder.getKeyZsetSchoolRank(schoolId);
                Integer lastRank = rankService.getLastRank(schoolRankKey);
                if (lastRank < personNum) {
                    //入围
                    redisService.zAdd(schoolRankKey, userId, totalScore);
                    redisService.hmSet(studentKey, schoolId, true);
                    break;
                }
            }
        }
    }

}
