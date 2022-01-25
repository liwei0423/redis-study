package com.liwei.redisstudy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
        List<RegionVO> regionVOS1 = new ArrayList<>();
        regionVOS1.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS1.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("123").build());
        list1.add(RecruitVO.builder().codeZone("111").zoneName("洪山青山区域").regionList(regionVOS1).build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "光谷二高", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("光谷三初").recruitList(list1).type("1").personNum(3).build()));

        List<RecruitVO> list2 = new ArrayList<>();
        List<RegionVO> regionVOS2 = new ArrayList<>();
        regionVOS2.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS2.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("123").build());
        list2.add(RecruitVO.builder().codeZone("222").zoneName("武汉市洪山区").regionList(regionVOS2).build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "华师一附中", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("光谷实验中学").recruitList(list2).type("1").personNum(2).build()));

        List<RecruitVO> list3 = new ArrayList<>();
        List<RegionVO> regionVOS3 = new ArrayList<>();
        regionVOS3.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS3.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("p8").build());
        list3.add(RecruitVO.builder().codeZone("333").zoneName("武汉市洪山区").regionList(regionVOS3).build());

        List<RecruitVO> list4 = new ArrayList<>();
        List<RegionVO> regionVOS4 = new ArrayList<>();
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("佛祖岭街道").build());
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("光谷大道").build());
        list4.add(RecruitVO.builder().codeZone("333").zoneName("东湖高新区").regionList(regionVOS4).build());
        redisService.hmSet(RedisKeyBuilder.getKeyHashSchoolRecruit(examId), "洪山高中", studentRecruitJsonString(SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list3).type("1").personNum(2).build(), SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list4).type("2").personNum(2).build()));
    }

    private static String studentRecruitJsonString(SchoolInfoVO... schoolIds) {
        List<SchoolInfoVO> list = new ArrayList<>();
        for (SchoolInfoVO schoolInfoVO : schoolIds) {
            list.add(schoolInfoVO);
        }
        return JSONArray.toJSONString(list);
    }

    private static List<SchoolInfoVO> studentRecruitToList(SchoolInfoVO... schoolIds) {
        List<SchoolInfoVO> list = new ArrayList<>();
        for (SchoolInfoVO schoolInfoVO : schoolIds) {
            list.add(schoolInfoVO);
        }
        return list;
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
        List<StudentRankVO> list = rankService.schoolStudentList(examId, null, null, "1");
        for (StudentRankVO studentRankVO : list) {
            System.out.println(studentRankVO);
        }
    }

    @Test
    public void testJson() {
        List<RecruitVO> list4 = new ArrayList<>();
        List<RegionVO> regionVOS4 = new ArrayList<>();
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("佛祖岭街道").build());
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("光谷大道").build());
        list4.add(RecruitVO.builder().codeZone("333").zoneName("武汉市洪山区").regionList(regionVOS4).build());
        String jsonStr = studentRecruitJsonString(SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list4).type("1").personNum(2).build());
        System.out.println(jsonStr);
    }

    @Test
    public void initMemory() {
        List<StudentInfoVO> studentList = new ArrayList<>();
        studentList.add(StudentInfoVO.builder().userId("p1").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("1").nameCn("张三").build());
        studentList.add(StudentInfoVO.builder().userId("p2").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("2").nameCn("李四").build());
        studentList.add(StudentInfoVO.builder().userId("p3").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("3").nameCn("王五").build());
        studentList.add(StudentInfoVO.builder().userId("p4").schoolId("光谷三初").town("佛祖岭街道").area("洪山区").orderNumber("4").nameCn("赵六").build());
        studentList.add(StudentInfoVO.builder().userId("p6").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("6").nameCn("张2").build());
        studentList.add(StudentInfoVO.builder().userId("p7").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("7").nameCn("张3").build());
        studentList.add(StudentInfoVO.builder().userId("p7").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("7").nameCn("张3").build());
        studentList.add(StudentInfoVO.builder().userId("p8").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("8").nameCn("张4").build());
        studentList.add(StudentInfoVO.builder().userId("p9").schoolId("光谷实验中学").town("光谷大道").area("洪山区").orderNumber("9").nameCn("张5").build());
        Map<String, List<SchoolInfoVO>> map = new HashMap<>();
        List<RecruitVO> list1 = new ArrayList<>();
        List<RegionVO> regionVOS1 = new ArrayList<>();
        regionVOS1.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS1.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("123").build());
        list1.add(RecruitVO.builder().codeZone("111").zoneName("洪山青山区域").regionList(regionVOS1).build());
        map.put("光谷二高", studentRecruitToList(SchoolInfoVO.builder().schoolId("光谷三初").recruitList(list1).type("1").personNum(3).build()));
        List<RecruitVO> list2 = new ArrayList<>();
        List<RegionVO> regionVOS2 = new ArrayList<>();
        regionVOS2.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS2.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("123").build());
        list2.add(RecruitVO.builder().codeZone("222").zoneName("武汉市洪山区").regionList(regionVOS2).build());
        map.put("华师一附中", studentRecruitToList(SchoolInfoVO.builder().schoolId("光谷实验中学").recruitList(list2).type("1").personNum(2).build()));
        List<RecruitVO> list3 = new ArrayList<>();
        List<RegionVO> regionVOS3 = new ArrayList<>();
        regionVOS3.add(RegionVO.builder().regionLevel(RegionLevel.AREA).region("洪山区").build());
        regionVOS3.add(RegionVO.builder().regionLevel(RegionLevel.STUDENT_CODE).region("p8").build());
        list3.add(RecruitVO.builder().codeZone("333").zoneName("武汉市洪山区").regionList(regionVOS3).build());
        List<RecruitVO> list4 = new ArrayList<>();
        List<RegionVO> regionVOS4 = new ArrayList<>();
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("佛祖岭街道").build());
        regionVOS4.add(RegionVO.builder().regionLevel(RegionLevel.TOWN).region("光谷大道").build());
        list4.add(RecruitVO.builder().codeZone("333").zoneName("东湖高新区").regionList(regionVOS4).build());
        map.put("洪山高中", studentRecruitToList(SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list3).type("1").personNum(2).build(), SchoolInfoVO.builder().schoolId("洪山高中").recruitList(list4).type("2").personNum(2).build()));
        rankService.initMemory(examId, studentList, map);
    }

}
