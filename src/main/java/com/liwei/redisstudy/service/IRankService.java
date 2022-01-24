package com.liwei.redisstudy.service;

import com.liwei.redisstudy.vo.SchoolInfoVO;
import com.liwei.redisstudy.vo.StudentInfoVO;
import com.liwei.redisstudy.vo.StudentRankVO;
import com.liwei.redisstudy.vo.StudentWillVO;

import java.util.List;
import java.util.Map;

/**
 * @description: 排名服务类
 * @author: liwei
 * @date: 2021/11/15
 */
public interface IRankService {

    /**
     * 内存初始化，实时排名前调用一次
     *
     * @param examId
     * @param studentList
     * @param schoolList
     * @return
     */
    boolean initMemory(String examId, List<StudentInfoVO> studentList, List<SchoolInfoVO> schoolList);

    /**
     * 清理内存，投档完成可调用释放redis内存资源
     *
     * @param examId
     * @return
     */
    boolean clearMemory(String examId);

    /**
     * 执行入围排名，可定时30s调用一次
     *
     * @param examId
     * @return
     */
    boolean executeRank(String examId);

    /**
     * 获取学校入围最后一名的学生信息
     *
     * @param examId   必填
     * @param schoolId 非必填
     * @param type     非必填
     * @return
     */
    List<StudentRankVO> getSchoolLastRank(String examId, String schoolId, String type);

    /**
     * 获取学生在学校的名次
     *
     * @param examId   必填
     * @param schoolId 必填
     * @param userId   必填
     * @return null 未入围
     */
    StudentRankVO mySchoolRank(String examId, String schoolId, String userId);

    /**
     * 获取学校入围学生清单
     *
     * @param examId   必填
     * @param schoolId 非必填
     * @param region   非必填
     * @param type     非必填
     * @return
     */
    List<StudentRankVO> schoolStudentList(String examId, String schoolId, String region, String type);

    /**
     * 学生志愿
     *
     * @param examId
     * @param userId
     * @param schoolList
     * @return
     */
    void studentWill(String examId, String userId, List<StudentWillVO> schoolList);

    /**
     * 批量学生志愿
     *
     * @param examId
     * @param map:   key为userId
     * @return
     */
    void batchStudentWill(String examId, Map<String, List<StudentWillVO>> map);

    /**
     * 更改学生的基本信息
     *
     * @param examId
     * @param studentInfoVO
     * @return
     */
    boolean updateStudentInfo(String examId, StudentInfoVO studentInfoVO);

    /**
     * 更改学校招生信息
     *
     * @param examId
     * @param schoolInfoVOS
     * @return
     */
    boolean updateSchoolInfo(String examId, List<SchoolInfoVO> schoolInfoVOS);
}
