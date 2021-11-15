package com.liwei.redisstudy.constant;

import java.text.MessageFormat;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/12
 */
public class RedisKeyBuilder {

    /**
     * @desc: 学生志愿
     */
    private final static String KEY_HASH_STUDENT = "student:{0}";

    /**
     * @desc: 学校信息
     */
    private final static String KEY_HASH_SCHOOL = "school:{0}";

    /**
     * @desc: 学校投档排名
     */
    public final static String KEY_ZSET_SCHOOL_RANK = "school_rank:{0}";

    /**
     * 获取学生志愿key
     *
     * @param userId
     */
    public static String getKeyHashStudent(String userId) {
        return MessageFormat.format(KEY_HASH_STUDENT, userId);
    }

    /**
     * 获取学校信息key
     *
     * @param schoolId
     */
    public static String getKeyHashSchool(String schoolId) {
        return MessageFormat.format(KEY_HASH_SCHOOL, schoolId);
    }

    /**
     * 获取学校信息key
     *
     * @param schoolId
     */
    public static String getKeyZsetSchoolRank(String schoolId) {
        return MessageFormat.format(KEY_ZSET_SCHOOL_RANK, schoolId);
    }

}
