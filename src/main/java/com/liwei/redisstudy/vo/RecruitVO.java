package com.liwei.redisstudy.vo;

import com.liwei.redisstudy.enums.RegionLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * 招生区域代码
     */
    private String codeZone;

    //招生区域名称
    private String zoneName;

    /**
     * 招生区域明细
     */
    List<RegionVO> regionList;

}
