package com.qsj.acoj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交状态枚举
 */
public enum QuestionSubmitStatusEnum {
    
    WAITING(0, "待判题"),
    RUNNING(1, "判题中"),
    ACCEPTED(2, "通过"),
    WRONG_ANSWER(3, "答案错误"),
    COMPILE_ERROR(4, "编译错误"),
    TIME_LIMIT_EXCEEDED(5, "超时"),
    MEMORY_LIMIT_EXCEEDED(6, "内存超限"),
    PRESENTATION_ERROR(7, "展示错误"),
    OUTPUT_LIMIT_EXCEEDED(8, "输出超限"),
    DANGEROUS_OPERATION(9, "危险操作"),
    RUNTIME_ERROR(10, "运行错误"),
    SYSTEM_ERROR(11, "系统错误");

    private final Integer value;

    private final String text;

    QuestionSubmitStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitStatusEnum anEnum : QuestionSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
