package com.liwei.redisstudy.constant;

import java.text.MessageFormat;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/12
 */
public class RedisKeyBuilder {

    /**
     * @desc: 学生分数明细
     */
    public final static String KEY_ZSET_STUDENT_SCORE = "student_score:{0}";
    /**
     * @desc: 学校投档学生排名
     */
    public final static String KEY_ZSET_SCHOOL_STUDENT = "school_student:{0}";

    /**
     * @desc: 学校投档排名
     */
    public final static String KEY_ZSET_SCHOOL_RANK = "school_rank:{0}:{1}";

    /**
     * @desc: 学生志愿
     */
    public final static String KEY_HASH_STUDENT = "student:{0}";

    /**
     * @desc: 学生志愿结果
     */
    public final static String KEY_HASH_STUDENT_RESULT = "student:result:{0}";

    /**
     * @desc: 学校信息
     */
    public final static String KEY_HASH_SCHOOL = "school:{0}";

    /**
     * 获取所有学生分数明细key
     *
     * @param examId
     */
    public static String getKeyZsetStudentScore(String examId) {
        return MessageFormat.format(KEY_ZSET_STUDENT_SCORE, examId);
    }

    /**
     * 获取学校投档学生排名key
     *
     * @param examId
     */
    public static String getKeyZsetSchoolStudent(String examId) {
        return MessageFormat.format(KEY_ZSET_SCHOOL_STUDENT, examId);
    }

    /**
     * 获取学生志愿key
     *
     * @param examId
     */
    public static String getKeyHashStudent(String examId) {
        return MessageFormat.format(KEY_HASH_STUDENT, examId);
    }

    /**
     * 获取学生志愿结果key
     *
     * @param examId
     */
    public static String getKeyHashStudentResult(String examId) {
        return MessageFormat.format(KEY_HASH_STUDENT_RESULT, examId);
    }

    /**
     * 获取学校信息key
     *
     * @param examId
     */
    public static String getKeyHashSchool(String examId) {
        return MessageFormat.format(KEY_HASH_SCHOOL, examId);
    }

    /**
     * 获取学校信息key
     *
     * @param examId
     * @param schoolId
     */
    public static String getKeyZsetSchoolRank(String examId, String schoolId) {
        return MessageFormat.format(KEY_ZSET_SCHOOL_RANK, examId, schoolId);
    }

}
