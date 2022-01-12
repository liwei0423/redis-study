package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/17
 */
@Data
@AllArgsConstructor
public class SchoolInfoVO {

    //学校编码
    private String schoolId;

    //招生区域
    private String region;

    //招生区域级别
    private int regionLevel;

    //招生种类
    private String type;

    //招生人数
    private int personNum;
}
