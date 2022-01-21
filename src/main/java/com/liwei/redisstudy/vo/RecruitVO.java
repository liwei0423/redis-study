package com.liwei.redisstudy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 招生信息
 * @author: liwei
 * @date: 2022/1/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitVO {

    /**
     *  招生区域级别：1省；2市；3区县；4乡镇；5学校；6学生编码
     */
    private int regionLevel;

    /**
     * 招生区域
     */
    private String region;

}
