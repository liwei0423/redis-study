package com.liwei.redisstudy.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.enums.RegionLevel;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 排名实现类
 * @author: liwei
 * @date: 2021/11/15
 */
@Slf4j
@Service
public class RankServiceImpl implements IRankService {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean initMemory(String examId, List<StudentInfoVO> studentList, List<SchoolInfoVO> schoolList) {
        //装载学生分数
        Map<Object, Object> studentScoreList = new HashMap<>();
        for (StudentInfoVO studentInfoVO : studentList) {
            studentScoreList.put(studentInfoVO.getUserId(), JSON.toJSONString(studentInfoVO));
        }
        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        redisService.hmBatchSet(studentInfoKey, studentScoreList);
        //装载学校招生
        Map<Object, Object> map = new HashMap<>();
        for (SchoolInfoVO schoolInfoVO : schoolList) {
            map.put(schoolInfoVO.getSchoolId(), JSON.toJSONString(schoolInfoVO));
        }
        String keyHashSchoolRecruit = RedisKeyBuilder.getKeyHashSchoolRecruit(examId);
        redisService.hmBatchSet(keyHashSchoolRecruit, map);
        return true;
    }

    @Override
    public boolean clearMemory(String examId) {
        //清理学生分数
        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        if (redisService.exists(studentInfoKey)) {
            redisService.remove(studentInfoKey);
        }
        //清理学生志愿
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.remove(keyHashStudent);
        //清理学校招生
        String keyHashSchoolRecruit = RedisKeyBuilder.getKeyHashSchoolRecruit(examId);
        redisService.remove(keyHashSchoolRecruit);
        //清理学校入围排名
        String keyZsetSchoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, "*", "*", "*");
        Set<String> schoolRankSets = redisService.keys(keyZsetSchoolRankPattern);
        redisService.removeBatch(schoolRankSets);
        //清理学生入围
        String keyHashStudentResult = RedisKeyBuilder.getKeyHashStudentResult(examId);
        redisService.remove(keyHashStudentResult);
        return true;
    }

    @Override
    public boolean executeRank(String examId) {
        //初始化
        init(examId);
        String calcTime = String.valueOf(System.currentTimeMillis());
        String studentResultKey = RedisKeyBuilder.getKeyHashStudentResult(examId);
        String studentKey = RedisKeyBuilder.getKeyHashStudent(examId);
        String keyHashSchoolRecruit = RedisKeyBuilder.getKeyHashSchoolRecruit(examId);
        Map<String, List<Object>> schoolRankMap = new HashMap<>(10);
        Map<Object, Object> studentRankMap = new HashMap<>(1000);
        Map<Object, Object> studentWillMap = redisService.hmGetTall(studentKey);
        Map<Object, Object> schoolInfoMap = redisService.hmGetTall(keyHashSchoolRecruit);
        //学生由高到底
        List<StudentInfoVO> studentInfoVOS = studentScoreOrder(examId);
        for (StudentInfoVO studentInfoVO : studentInfoVOS) {
            String userId = studentInfoVO.getUserId();
            String orderNumber = studentInfoVO.getOrderNumber();
            //学生所在区域
            String area = studentInfoVO.getArea();
            //学生志愿信息
            List<StudentWillVO> studentWillVOS = JSONObject.parseArray((String) studentWillMap.get(userId), StudentWillVO.class);
            nextStudent:
            for (StudentWillVO studentWillVO : studentWillVOS) {
                //学生志愿校
                String schoolId = studentWillVO.getSchoolId();
                //学生志愿种类
                String type = studentWillVO.getType();
                List<SchoolInfoVO> schoolInfoList = JSON.parseArray((String) schoolInfoMap.get(schoolId), SchoolInfoVO.class);
                //学生填报学校的招生信息
                SchoolInfoVO schoolInfoVO = getStudentSchoolInfo(schoolInfoList, type);
                if (schoolInfoVO == null) {
                    //学校无此招生信息
                    continue;
                }
                //招生人数
                Integer personNum = schoolInfoVO.getPersonNum();
                //招生区域集合
                List<RecruitVO> recruitList = schoolInfoVO.getRecruitList();
                for (RecruitVO recruitVO : recruitList) {
                    if (isMatchSchoolInfoRegion(studentInfoVO, recruitVO)) {
                        //符合学校区域招生
                        String schoolRankMapKey = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId, type, recruitVO.getRegion());
                        StudentRankVO lastStudentRankVO = getSchoolLastStudent(schoolRankMapKey, schoolRankMap);
                        if (lastStudentRankVO == null || lastStudentRankVO.getRank() < personNum) {
                            //入围
                            List<Object> list;
                            if (schoolRankMap.containsKey(schoolRankMapKey)) {
                                list = schoolRankMap.get(schoolRankMapKey);
                            } else {
                                list = new ArrayList();
                            }
                            Integer rank = lastStudentRankVO == null ? 1 : (orderNumber.compareTo(lastStudentRankVO.getOrderNumber()) < 0 ? lastStudentRankVO.getRank() + 1 : lastStudentRankVO.getRank());
                            StudentRankVO currentStudentRankVO = StudentRankVO.builder().userId(userId).orderNumber(orderNumber).rank(rank).wishId(studentWillVO.getWishId()).schoolId(schoolId)
                                    .region(recruitVO.getRegion()).regionLevel(recruitVO.getRegionLevel().getCode()).type(type).calcTime(calcTime).recruitCode(schoolInfoVO.getRecruitCode())
                                    .schoolName(schoolInfoVO.getSchoolName()).zoneName(schoolInfoVO.getZoneName()).recruitKindName(schoolInfoVO.getRecruitKindName()).build();
                            currentStudentRankVO.setStudentInfoVO(studentInfoVO);
                            list.add(JSON.toJSONString(currentStudentRankVO));
                            schoolRankMap.put(schoolRankMapKey, list);
                            studentRankMap.put(userId, JSON.toJSONString(currentStudentRankVO));
                            break nextStudent;
                        }
                    }
                }
            }
        }
        //批量写入redis
        for (String key : schoolRankMap.keySet()) {
            redisService.lPushBatch(key, schoolRankMap.get(key));
        }
        redisService.hmBatchSet(studentResultKey, studentRankMap);
        return true;
    }


    private boolean isMatchSchoolInfoRegion(StudentInfoVO studentInfoVO, RecruitVO recruitVO) {
        RegionLevel regionLevel = recruitVO.getRegionLevel();
        String region = recruitVO.getRegion();
        switch (regionLevel) {
            case PROVINCE:
            case CITY:
                return true;
            case AREA:
                return region.equals(studentInfoVO.getArea());
            case TOWN:
                return region.equals(studentInfoVO.getTown());
            case SCHOOL:
                return region.equals(studentInfoVO.getSchoolId());
            case STUDENT_CODE:
                return region.equals(studentInfoVO.getUserId());
        }
        return false;
    }

    private SchoolInfoVO getStudentSchoolInfo(List<SchoolInfoVO> schoolInfoList, String type) {
        if (CollUtil.isNotEmpty(schoolInfoList)) {
            for (SchoolInfoVO schoolInfoVO : schoolInfoList) {
                if (schoolInfoVO.getType().equals(type)) {
                    return schoolInfoVO;
                }
            }
        }
        return null;
    }

    public List<StudentInfoVO> studentScoreOrder(String examId) {
        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        Map<Object, Object> studentInfoMap = redisService.hmGetTall(studentInfoKey);
        List<StudentInfoVO> list = new ArrayList<>();
        for (Object userId : studentInfoMap.keySet()) {
            StudentInfoVO studentInfoVO = JSON.parseObject((String) studentInfoMap.get(userId), StudentInfoVO.class);
            list.add(studentInfoVO);
        }
        List<StudentInfoVO> sortedList = list.stream().sorted(Comparator.comparing(StudentInfoVO::getOrderNumber).reversed()).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * 从List集合获取学校最后一名的学生信息
     *
     * @param schoolRankMapKey
     * @param schoolRankMap
     * @return
     */
    private StudentRankVO getSchoolLastStudent(String schoolRankMapKey, Map<String, List<Object>> schoolRankMap) {
        if (schoolRankMap.containsKey(schoolRankMapKey)) {
            List<Object> list = schoolRankMap.get(schoolRankMapKey);
            if (list.size() > 0) {
                String studentRankStr = (String) list.get(list.size() - 1);
                StudentRankVO studentRankVO = JSONObject.parseObject(studentRankStr, StudentRankVO.class);
                return studentRankVO;
            }
        }
        return null;
    }

    /**
     * 初始化，清理学生入围信息和学校已有排名
     *
     * @param
     * @return
     */
    private void init(String examId) {
        String keyHashStudentResult = RedisKeyBuilder.getKeyHashStudentResult(examId);
        redisService.remove(keyHashStudentResult);

        String schoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, "*", "*", "*");
        Set<String> schoolRankSets = redisService.keys(schoolRankPattern);
        redisService.removeBatch(schoolRankSets);
    }

    @Override
    public List<StudentRankVO> getSchoolLastRank(String examId, String schoolId, String type) {
        List resultList = new ArrayList();

        String schoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId == null ? "*" : schoolId, type == null ? "*" : type, "*");
        Set<String> schoolRankSet = redisService.keys(schoolRankPattern);
        if (CollUtil.isNotEmpty(schoolRankSet)) {
            for (String schoolRankKey : schoolRankSet) {
                Object schoolRankObject = redisService.lLast(schoolRankKey);
                StudentRankVO studentRankVO = JSON.parseObject((String) schoolRankObject, StudentRankVO.class);
                resultList.add(studentRankVO);
            }
        }
        return resultList;
    }

    @Override
    public StudentRankVO mySchoolRank(String examId, String schoolId, String userId) {
        String studentResultKey = RedisKeyBuilder.getKeyHashStudentResult(examId);
        Object studentResultObject = redisService.hmGet(studentResultKey, userId);
        StudentRankVO studentRankVO = JSON.parseObject((String) studentResultObject, StudentRankVO.class);
        if (studentRankVO != null && studentRankVO.getSchoolId().equals(schoolId)) {
            return studentRankVO;
        }
        return null;
    }

    @Override
    public List<List<StudentRankVO>> schoolStudentList(String examId, String schoolId, String region, String type) {
        List<List<StudentRankVO>> resultList = new ArrayList<>();
        String schoolRankPattern = RedisKeyBuilder.getKeyZsetSchoolRank(examId, schoolId == null ? "*" : schoolId, type == null ? "*" : type, region == null ? "*" : region);
        String keyHashStudentInfo = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        Map<Object, Object> studentInfoMap = redisService.hmGetTall(keyHashStudentInfo);
        Set<String> schoolRankSet = redisService.keys(schoolRankPattern);
        if (CollUtil.isNotEmpty(schoolRankSet)) {
            for (String schoolRankKey : schoolRankSet) {
                List<Object> schoolRankList = redisService.lList(schoolRankKey);
                List<StudentRankVO> studentRankList = new ArrayList<>();
                for (Object value : schoolRankList) {
                    StudentRankVO studentRankVO = JSON.parseObject((String) value, StudentRankVO.class);
                    StudentInfoVO studentInfoVO = JSON.parseObject((String) studentInfoMap.get(studentRankVO.getUserId()), StudentInfoVO.class);
                    studentRankVO.setStudentInfoVO(studentInfoVO);
                    studentRankList.add(studentRankVO);
                }
                resultList.add(studentRankList);
            }
        }
        return resultList;
    }

    @Override
    public void studentWill(String examId, String userId, List<StudentWillVO> schoolList) {
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.hmSet(keyHashStudent, userId, JSON.toJSONString(schoolList));
    }

    @Override
    public void batchStudentWill(String examId, Map<String, List<StudentWillVO>> map) {
        if (CollUtil.isNotEmpty(map)) {
            Map<Object, Object> valueMap = new HashMap<>();
            for (String userId : map.keySet()) {
                valueMap.put(userId, JSON.toJSONString(map.get(userId)));
            }
            String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
            redisService.hmBatchSet(keyHashStudent, valueMap);
        }
    }

    @Override
    public boolean updateStudentInfo(String examId, StudentInfoVO studentInfoVO) {
        if (studentInfoVO != null && studentInfoVO.getUserId() != null) {
            String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
            redisService.hmSet(studentInfoKey, studentInfoVO.getUserId(), JSON.toJSONString((studentInfoVO)));
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSchoolInfo(String examId, List<SchoolInfoVO> schoolInfoVOS) {
        if (!CollectionUtils.isEmpty(schoolInfoVOS)) {
            redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), schoolInfoVOS.get(0).getSchoolId(), JSON.toJSONString(schoolInfoVOS));
            return true;
        }
        return false;
    }

}
