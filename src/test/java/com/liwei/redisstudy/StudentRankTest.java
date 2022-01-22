package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.liwei.redisstudy.constant.RedisKeyBuilder;
import com.liwei.redisstudy.enums.RegionLevel;
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
        redisService.hmSet(studentInfoKey, "p1", JSON.toJSONString(StudentInfoVO.builder().userId("p1").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("1").nameCn("张三").build()));
        redisService.hmSet(studentInfoKey, "p2", JSON.toJSONString(StudentInfoVO.builder().userId("p2").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("2").nameCn("李四").build()));
        redisService.hmSet(studentInfoKey, "p3", JSON.toJSONString(StudentInfoVO.builder().userId("p3").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("3").nameCn("王五").build()));
        redisService.hmSet(studentInfoKey, "p4", JSON.toJSONString(StudentInfoVO.builder().userId("p4").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("4").nameCn("赵六").build()));
        redisService.hmSet(studentInfoKey, "p5", JSON.toJSONString(StudentInfoVO.builder().userId("p5").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("5").nameCn("张1").build()));
        redisService.hmSet(studentInfoKey, "p6", JSON.toJSONString(StudentInfoVO.builder().userId("p6").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("6").nameCn("张2").build()));
        redisService.hmSet(studentInfoKey, "p7", JSON.toJSONString(StudentInfoVO.builder().userId("p7").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("7").nameCn("张3").build()));
        redisService.hmSet(studentInfoKey, "p8", JSON.toJSONString(StudentInfoVO.builder().userId("p8").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("8").nameCn("张4").build()));
        redisService.hmSet(studentInfoKey, "p9", JSON.toJSONString(StudentInfoVO.builder().userId("p9").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("9").nameCn("张5").build()));
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

        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p1", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷二高").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("洪山高中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p2", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷二高").type("1").build(), StudentWillVO.builder().wishId("103").schoolId("洪山高中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p3", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷二高").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("华师一附中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p4", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷二高").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("华师一附中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p5", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷二高").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("洪山高中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p6", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷实验中学").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("洪山高中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p7", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷实验中学").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("洪山高中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p8", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷实验中学").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("华师一附中").type("1").build()));
        redisService.hmSet(RedisKeyBuilder.getKeyHashStudent(examId), "p9", studentWillJsonString(StudentWillVO.builder().wishId("101").schoolId("光谷实验中学").type("1").build(), StudentWillVO.builder().wishId("102").schoolId("洪山高中").type("1").build()));
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
        list1.add(RecruitVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "光谷二高", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("光谷三初").recruitList(list1).type("1").personNum(3).build()));

        List<RecruitVO> list2 = new ArrayList<>();
        list2.add(RecruitVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "华师一附中", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("光谷实验中学").recruitList(list2).type("1").personNum(2).build()));

        List<RecruitVO> list3 = new ArrayList<>();
        list3.add(RecruitVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        list3.add(RecruitVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("p8").build());

        List<RecruitVO> list4 = new ArrayList<>();
        list4.add(RecruitVO.builder().regionLevel(RegionLevel.TOWN).region("佛祖岭街道").build());
        list4.add(RecruitVO.builder().regionLevel(RegionLevel.TOWN).region("光谷大道").build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "洪山高中", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list3).type("1").personNum(2).build(), SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list4).type("2").personNum(2).build()));
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
        StudentRankVO studentRankVO = rankService.mySchoolRank(examId, "光谷二高", "p4");
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
