package com.liwei.redisstudy.service;

import com.liwei.redisstudy.vo.StudentRankVO;

import java.util.List;

/**
 * @description: 排名服务类
 * @author: liwei
 * @date: 2021/11/15
 */
public interface IRankService {

    /**
     * 内存初始化，只调用一次
     *
     * @param examId
     * @return
     */
    boolean initRank(String examId);

    /**
     * 执行入围排名，可定时30s调用一次
     *
     * @param
     * @return
     */
    boolean executeRank(String examId);

    /**
     * 获取学校入围最后一名的学生信息
     *
     * @param schoolId
     * @return
     */
    StudentRankVO getSchoolLastRank(String examId, String schoolId);

    /**
     * 获取学生在学校的名次
     *
     * @param schoolId
     * @param userId
     * @return -1 未入围
     */
    Integer mySchoolRank(String examId, String schoolId, String userId);

    /**
     * 获取学校入围学生清单
     *
     * @param schoolId
     * @return
     */
    List<String> schoolStudentList(String examId, String schoolId);

    /**
     * 学生提交志愿
     *
     * @param userId
     * @param schoolList
     * @return
     */
    void studentSubmitWill(String examId, String userId, List<String> schoolList);

    /**
     * 更新学生的分数
     *
     * @param userId
     * @param score
     * @return
     */
    void updateStudentScore(String examId, String userId, double score);

    /**
     * 更新学校招生人数
     *
     * @param schoolId
     * @param personNum
     * @return
     */
    void updateSchoolInfo(String examId, String schoolId, Integer personNum);
}
