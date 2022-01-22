package com.liwei.redisstudy.vo;

import com.liwei.redisstudy.enums.RegionLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 招生区域
 * @author: liwei
 * @date: 2022/1/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitVO {

    /**
     *  招生区域级别：1 省；2 市；3 区县；4 乡镇；5 学校；6 学生编码
     */
    private RegionLevel regionLevel;

    /**
     * 招生区域
     */
    private String region;

}
