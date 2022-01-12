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
public class StudentWillVO {

    private String schoolId;

    //招生种类
    private String type;

}
