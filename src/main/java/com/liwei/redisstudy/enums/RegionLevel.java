package com.liwei.redisstudy.enums;

/**
 * @description: 地区级别
 * @author: liwei
 * @date: 2022/1/22
 */
public enum RegionLevel {

    PROVINCE(1, "省"),

    CITY(2, "市"),

    AREA(3, "区县"),

    TOWN(4, "乡镇"),

    SCHOOL(5, "校"),

    STUDENT_CODE(6, "学生编码");

    private final Integer code;

    private final String name;

    RegionLevel(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RegionLevel getByCode(Integer code) {
        for (RegionLevel regionLevel : RegionLevel.values()) {
            if (regionLevel.getCode().equals(code)) {
                return regionLevel;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
