package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 学生信息
 * @author: liwei
 * @date: 2022/1/11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StudentInfoVO implements Comparable<StudentInfoVO> {

    /******主属性 必备*********/
    //学生ID
    private String userId;

    //所在学校
    private String schoolId;

    //所在乡镇
    private String town;

    //所在区县
    private String area;

    //排名参照
    private String orderNumber;


    /******副属性 *********/

    //学生报名号
    private String signCode;

    //学生准考证号
    private String permitId;

    //姓名
    private String nameCn;

    //总分
    private String zcj;

    //等级组合
    private String djzh;

    //语文
    private String yw;

    //数学
    private String sx;


    //英语
    private String yy;

    //物理
    private String wl;

    //化学
    private String hx;

    //政治
    private String zz;

    //历史
    private String ls;

    //地理
    private String dl;

    //生物
    private String sw;




    @Override
    public int compareTo(StudentInfoVO o) {
        return this.orderNumber.compareTo(o.getOrderNumber());
    }
}
