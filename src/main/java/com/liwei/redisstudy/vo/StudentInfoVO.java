package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: liwei
 * @date: 2022/1/11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentInfoVO implements Comparable<StudentInfoVO> {

    //学生ID
    private String userId;

    //学生编码
    private String userCode;

    //所在学校
    private String schoolId;

    //所在乡镇
    private String town;

    //所在区县
    private String area;

    //排名参照
    private String orderNumber;

    @Override
    public int compareTo(StudentInfoVO o) {
        return this.orderNumber.compareTo(o.getOrderNumber());
    }
}
