package com.liwei.redisstudy.vo;

import lombok.*;

/**
 * @description:
 * @author: liwei
 * @date: 2021/11/15
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentRankVO {

    private String userId;

    private String orderNumber;

    private Integer rank;

    private String schoolId;

    private String region;

    private String type;
}
