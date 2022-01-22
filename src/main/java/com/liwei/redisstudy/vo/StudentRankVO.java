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

    private Integer regionLevel;

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
    private StudentInfoVO studentInfoVO;

}
