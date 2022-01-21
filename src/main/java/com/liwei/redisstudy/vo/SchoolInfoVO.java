package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 学校招生信息
 * @author: liwei
 * @date: 2021/11/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolInfoVO {

    /**
     *  学校编码
     */
    private String schoolId;

    /**
     *  招生种类
     */
    private String type;

    /**
     *  招生区域集合
     */
    private List<RecruitVO> recruitList;

    //招生人数
    private int personNum;


    /******副属性 *********/

    //招生代码
    private String recruitCode;

    //招生学校名称
    private String schoolName;

    //招生区域名称
    private String zoneName;

    //招生种类名称
    private String recruitKindName;


}
