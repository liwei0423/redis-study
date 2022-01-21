package com.liwei.redisstudy.vo;

import lombok.*;

/**
 * @description: 学生实时排名
 * @author: liwei
 * @date: 2021/11/15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StudentRankVO {

    private String userId;

    private String orderNumber;

    private Integer rank;

    //志愿id 志愿顺序 101
    private String wishId;

    private String schoolId;

    private String region;

    private String type;

    //开始计算时间
    private String calcTime;


    /**********学校副属性************/
    //招生代码
    private String recruitCode;

    //招生学校名称
    private String schoolName;

    //招生区域名称
    private String zoneName;

    //招生种类名称
    private String recruitKindName;



    /**********学生副属性************/
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



}
