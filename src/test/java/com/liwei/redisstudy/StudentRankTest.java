package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.service.IRankService;
import com.liwei.redisstudy.service.RedisService;
import com.liwei.redisstudy.vo.*;
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

    private final String examId = "examId";

    @Test
    public void studentScore() {

        String studentInfoKey = RedisKeyBuilder.getKeyHashStudentInfo(examId);
        redisService.remove(studentInfoKey);
        redisService.hmSet(studentInfoKey, "1", JSON.toJSONString(StudentInfoVO.builder().userId("1").schoolId("11").town("t1").area("a1").orderNumber("1234").nameCn("张三").build()));
        redisService.hmSet(studentInfoKey, "2", JSON.toJSONString(StudentInfoVO.builder().userId("2").schoolId("11").town("t1").area("a1").orderNumber("1111").nameCn("李四").build()));
        redisService.hmSet(studentInfoKey, "3", JSON.toJSONString(StudentInfoVO.builder().userId("3").schoolId("11").town("t1").area("a1").orderNumber("2222").nameCn("王五").build()));
        redisService.hmSet(studentInfoKey, "4", JSON.toJSONString(StudentInfoVO.builder().userId("4").schoolId("11").town("t1").area("a1").orderNumber("1212").nameCn("赵六").build()));
        redisService.hmSet(studentInfoKey, "5", JSON.toJSONString(StudentInfoVO.builder().userId("5").schoolId("11").town("t1").area("a1").orderNumber("2222").nameCn("张1").build()));
        redisService.hmSet(studentInfoKey, "6", JSON.toJSONString(StudentInfoVO.builder().userId("6").schoolId("11").town("t1").area("a1").orderNumber("2122").nameCn("张2").build()));
        redisService.hmSet(studentInfoKey, "7", JSON.toJSONString(StudentInfoVO.builder().userId("7").schoolId("11").town("t1").area("a1").orderNumber("1322").nameCn("张3").build()));
        redisService.hmSet(studentInfoKey, "8", JSON.toJSONString(StudentInfoVO.builder().userId("8").schoolId("11").town("t1").area("a1").orderNumber("1231").nameCn("张4").build()));
        redisService.hmSet(studentInfoKey, "9", JSON.toJSONString(StudentInfoVO.builder().userId("9").schoolId("11").town("t1").area("a1").orderNumber("2322").nameCn("张5").build()));
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

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "1", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("22").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "2", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("22").type("1").build(), StudentWillVO.builder().wishId("103").schoolId("33").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "3", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("22").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "4", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("33").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "5", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("22").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("33").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "6", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("22").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "7", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("33").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "8", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("22").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("33").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "9", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("11").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("22").type("1").build()));
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
        List<RecruitVO> list1 = new ArrayList<>();
        list1.add(RecruitVO.builder().regionLevel(1).region("430000").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "11", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("11").recruitList(list1).type("1").personNum(3).build()));

        List<RecruitVO> list2 = new ArrayList<>();
        list2.add(RecruitVO.builder().regionLevel(2).region("430070").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "22", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("22").recruitList(list2).type("1").personNum(2).build()));

        List<RecruitVO> list3 = new ArrayList<>();
        list3.add(RecruitVO.builder().regionLevel(1).region("430000").build());
        list3.add(RecruitVO.builder().regionLevel(1).region("437000").build());

        List<RecruitVO> list4 = new ArrayList<>();
        list4.add(RecruitVO.builder().regionLevel(5).region("11").build());
        list4.add(RecruitVO.builder().regionLevel(5).region("22").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "33", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("33").recruitList(list3).type("1").personNum(2).build(), SchoolInfoVO.builder().schoolId("33").recruitList(list4).type("2").personNum(2).build()));
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
        List<StudentRankVO> studentRankVOS = rankService.getSchoolLastRank(examId, null, null);
        studentRankVOS.forEach(System.out::println);
    }

    @Test
    public void testMySchoolRank() {
        StudentRankVO studentRankVO = rankService.mySchoolRank(examId, "22", "1");
        System.out.println(studentRankVO);
    }

    @Test
    public void testSchoolStudentList() {
        List<List<StudentRankVO>> list = rankService.schoolStudentList(examId, null, null, "1");
        for (List<StudentRankVO> studentRankVO : list) {
            System.out.println(studentRankVO);
        }
    }

}
