package com.kealliang.utils;

import org.springframework.beans.BeanUtils;

/**
 * @author lsr
 * @ClassName ModelMapper
 * @Date 2019-02-03
 * @Desc 对象映射工具
 * @Vertion 1.0
 */
public class ModelMapper {

    public <T> T map(Object source, Class<T> targetClass) {
        T instance = null;
        try {
            instance = targetClass.newInstance();
            BeanUtils.copyProperties(source, instance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public <T> T map(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
