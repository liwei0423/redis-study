package com.liwei.redisstudy.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/16
 */
@Setter
@Getter
public class StudentScoreVO {

    private String userId;

    private Double score;

    private String orderNumber;
}
