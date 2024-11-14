package com.qsj.acoj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author qsj
 * @Date 2024/11/13
 */
public enum ContestStatusEnum {

    PENDING("等待中", "1"),
    ONGOING("进行中", "2"),
    COMPLETED("已结束", "3");

    private final String text;
    private final String value;

    ContestStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ContestStatusEnum getEnumByValue(Integer value) {  // 修改此处
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ContestStatusEnum anEnum : ContestStatusEnum.values()) {  // 修改此处
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
