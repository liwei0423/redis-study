package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.SchoolInfoVO;
import com.liwei.redisstudy.vo.StudentInfoVO;
import com.liwei.redisstudy.vo.StudentRankVO;
import com.liwei.redisstudy.vo.StudentWillVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

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

    private final String examId = "1111";

    @Test
    public void studentScore() {

        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        redisService.remove(studentInfoKey);
        redisService.hmSet(studentInfoKey, "1", JSON.toJSONString(new StudentInfoVO("1", "1", "11", "t1", "a1", "1234")));
        redisService.hmSet(studentInfoKey, "2", JSON.toJSONString(new StudentInfoVO("2", "2", "11", "t1", "a1", "1111")));
        redisService.hmSet(studentInfoKey, "3", JSON.toJSONString(new StudentInfoVO("3", "3", "11", "t1", "a1", "2222")));
        redisService.hmSet(studentInfoKey, "4", JSON.toJSONString(new StudentInfoVO("4", "4", "11", "t1", "a1", "1212")));
        redisService.hmSet(studentInfoKey, "5", JSON.toJSONString(new StudentInfoVO("5", "5", "11", "t1", "a1", "2222")));
        redisService.hmSet(studentInfoKey, "6", JSON.toJSONString(new StudentInfoVO("6", "6", "11", "t1", "a1", "2122")));
        redisService.hmSet(studentInfoKey, "7", JSON.toJSONString(new StudentInfoVO("7", "7", "11", "t1", "a1", "1322")));
        redisService.hmSet(studentInfoKey, "8", JSON.toJSONString(new StudentInfoVO("8", "8", "11", "t1", "a1", "1231")));
        redisService.hmSet(studentInfoKey, "9", JSON.toJSONString(new StudentInfoVO("9", "9", "11", "t1", "a1", "2322")));
    }

    @Test
    public void studentScoreOrder() {
        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        Map<Object, Object> studentInfoMap = redisService.hmGetTall(studentInfoKey);
        List<StudentInfoVO> list = new ArrayList<>();
        for (Object userId : studentInfoMap.keySet()) {
            StudentInfoVO studentInfoVO = JSON.parseObject((String) studentInfoMap.get(userId), StudentInfoVO.class);
            list.add(studentInfoVO);
        }
        List<StudentInfoVO> sortedList = list.stream().sorted(Comparator.comparing(StudentInfoVO::getOrderNumber).reversed()).collect(Collectors.toList());
        sortedList.forEach(System.out::println);
    }


    @Test
    public void studentVolunteer() {
        String keyHashStudent = RedisKeyBuilder.getKeyHashStudent(examId);
        redisService.remove(keyHashStudent);

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "1", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("22", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "2", studentWillJsonString(new StudentWillVO("22", "1"), new StudentWillVO("33", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "3", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("22", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "4", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("33", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "5", studentWillJsonString(new StudentWillVO("22", "1"), new StudentWillVO("33", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "6", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("22", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "7", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("33", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "8", studentWillJsonString(new StudentWillVO("22", "1"), new StudentWillVO("33", "1")));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "9", studentWillJsonString(new StudentWillVO("11", "1"), new StudentWillVO("22", "1")));
    }

    private static String studentWillJsonString(StudentWillVO... schoolIds) {
        List<StudentWillVO> list = new ArrayList<>();
        for (StudentWillVO studentWillVO : schoolIds) {
            list.add(studentWillVO);
        }
        return JSON.toJSONString(list);
    }

    @Test
    public void schoolRecruit() {
        String keyHashSchoolRecruit = RedisKeyBuilder.getKeyHashSchoolRecruit(examId);
        redisService.remove(keyHashSchoolRecruit);

        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "11", studentRecruitJsonString(new SchoolInfoVO("11", "430000", 1, "1", 3)));
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "22", studentRecruitJsonString(new SchoolInfoVO("22", "430070", 2, "1", 2)));
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "33", studentRecruitJsonString(new SchoolInfoVO("33", "430000", 1, "1", 2), new SchoolInfoVO("33", "430000", 1, "2", 2)));
    }

    private static String studentRecruitJsonString(SchoolInfoVO... schoolIds) {
        List<SchoolInfoVO> list = new ArrayList<>();
        for (SchoolInfoVO schoolInfoVO : schoolIds) {
            list.add(schoolInfoVO);
        }
        return JSON.toJSONString(list);
    }

    @Test
    public void testZsetBatchAdd() {
        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        Map<String, Double> map = new HashMap<>();
        map.put("1", 80.0);
        map.put("2", 81.0);
        redisService.zBatchAdd(studentInfoKey, map);
    }

    @Test
    public void testRemoveKeyBatch() {
        Set<String> keyList = new HashSet<>();
        keyList.add("key1");
        keyList.add("key2");
        redisService.removeBatch(keyList);
    }

    @Test
    public void clear() {
        rankService.clearMemory(examId);
    }

    @Test
    public void executeRank() {
        boolean flag = rankService.executeRank(examId);
        System.out.println("return=" + flag);
    }

    @Test
    public void getSchoolLastRank() {
        StudentRankVO studentRankVO = rankService.getSchoolLastRank(examId, "11", "430000", "1");
        System.out.println(studentRankVO);
    }

    @Test
    public void testMySchoolRank() {
        StudentRankVO studentRankVO = rankService.mySchoolRank(examId, "22", "1");
        System.out.println(studentRankVO);
    }

    @Test
    public void testSchoolStudentList() {
        List<StudentRankVO> list = rankService.schoolStudentList(examId, "11", "430000", "1");
        for (StudentRankVO studentRankVO : list) {
            System.out.println(studentRankVO);
        }
    }

}
