package com.sf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@Component
public class ObjectActionUtils {
    private static final Logger log = LoggerFactory.getLogger(ObjectActionUtils.class);


    /*对象属性拷贝通用方法，例如：UserDTO ==> User*/
    public static void copyByName(Object src, Object target) {
        if (src == null || target == null) {
            return;
        }
        try {
            Map<String, Field> srcFieldMap = getAssignableFieldsMap(src);
            Map<String, Field> targetFieldMap = getAssignableFieldsMap(target);
            for (String srcFieldName : srcFieldMap.keySet()) {
                Field srcField = srcFieldMap.get(srcFieldName);
                if (srcField == null) {
                    continue;
                }
                // 变量名需要相同
                if (!targetFieldMap.containsKey(srcFieldName)) {
                    continue;
                }
                Field targetField = targetFieldMap.get(srcFieldName);
                if (targetField == null) {
                    continue;
                }
                // 类型需要相同
                if (!srcField.getType().equals(targetField.getType())) {
                    continue;
                }
                targetField.set(target, srcField.get(src));
            }
        } catch (Exception e) {
            // 异常
        }
    }

    private static Map<String, Field> getAssignableFieldsMap(Object obj) {
        if (obj == null) {
            return new HashMap<String, Field>();
        }
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            // 过滤不需要拷贝的属性
            if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }

    /**
     *
     * 两个List集合操作(以整型为例)
     *
     */
    /*求List差集方法 list1 - list2*/
    public static List<Integer> listComplement(List<Integer> list1, List<Integer> list2) {
        return list1.stream().filter(item -> !list2.contains(item)).collect(toList());
    }

    /*求List交集方法 list1 ∩ list2*/
    public static List<Integer> listIntersection(List<Integer> list1, List<Integer> list2) {
        return list1.stream().filter(list2::contains).collect(toList());
    }

    /*求List并集方法 list1 ∪ list2*/
    public static List<Integer> listUnion(List<Integer> list1, List<Integer> list2, Boolean deduplication) {
        List<Integer> listAll = list1.parallelStream().collect(toList());
        List<Integer> listAll2 = list2.parallelStream().collect(toList());
        listAll.addAll(listAll2);
        if (deduplication) { // 判断是否去重
            return listAll.stream().distinct().collect(toList());
        } else {
            return listAll;
        }
    }

}
