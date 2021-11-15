package com.liwei.redisstudy.service;

import com.liwei.redisstudy.vo.StudentRankVO;

import java.util.List;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
public interface IRankService {

    /**
     * 执行入围排名，可定时30s调用一次
     *
     * @param
     * @return
     */
    boolean executeRank();

    /**
     * 计算学校入围最后一名的学生
     *
     * @param schoolId
     * @return
     */
    Integer executeSchoolLastRank(String schoolId);

    /**
     * 获取用户在学校的名次
     *
     * @param schoolId
     * @param userId
     * @return -1 未入围
     */
    Integer mySchoolRank(String schoolId, String userId);

    /**
     * 学校入围学生清单
     *
     * @param
     * @return
     */
    List<String> schoolStudentList(String schoolId);

    /**
     * 获取学校入围最后一名的学生信息
     *
     * @param schoolId
     * @return
     */
    StudentRankVO getSchoolLastRank(String schoolId);
}
