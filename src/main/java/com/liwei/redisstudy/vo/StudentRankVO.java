package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRankVO {

    private String userId;

    private Integer rank;
}
