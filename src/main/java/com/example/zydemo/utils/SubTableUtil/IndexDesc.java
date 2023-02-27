package com.example.zydemo.utils.SubTableUtil;


import java.lang.annotation.*;

/**
 * @auther weiruyue
 * @date 2022/9/3 14:00
 * @description 自定义索引注解
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface IndexDesc {
    /**
     * 不定义就获取字段名
     * @return
     */
    String fieldName() default "";

    /**
     * 定义索引类型 value  默认b+树
     * @return
     */
    IndexTypeEnum value() default IndexTypeEnum.BPlusTree;
}
