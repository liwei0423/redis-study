package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @description: 学生志愿
 * @author: liwei
 * @date: 2021/11/17
 */
@Data
@AllArgsConstructor
@Builder
public class StudentWillVO {

    //志愿id 101
    private String wishId;

    private String schoolId;

    //招生种类
    private String type;

}
