package com.tangxiaopeng.videoeditdemo.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:VideoEditDemo20181011
 * Date:2018/10/25 15:06
 *
 * @author fanqiejiang
 */

public class GsonTools {

    public GsonTools() {
    }

    //使用Gson进行解析Person
    public static <T> T getBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return t;
    }

    // 使用Gson进行解析 List<Person>
    public static <T> List<T> getListBean(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 将获取到的json字符串转换为对象集合进行返回
     *
     * @param jsonData 需要解析的json字符串
     * @param cls      类模板
     * @return
     */
    public static <T> List<T> getObjList(String jsonData, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        if (jsonData.startsWith("[") && jsonData.endsWith("]")) {//当字符串以“[”开始，以“]”结束时，表示该字符串解析出来为集合
            //截取字符串，去除中括号
            jsonData = jsonData.substring(1, jsonData.length() - 1);
            //将字符串以"},"分解成数组
            String[] strArr = jsonData.split("\\},");
            //分解后的字符串数组的长度
            int strArrLength = strArr.length;
            //遍历数组，进行解析，将字符串解析成对象
            for (int i = 0; i < strArrLength; i++) {
                String newJsonString = null;
                if (i == strArrLength - 1) {
                    newJsonString = strArr[i];
                } else {
                    newJsonString = strArr[i] + "}";
                }
                T bean = getObj(newJsonString, cls);
                list.add(bean);
            }
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return list;
    }

    /**
     * 将传入的json字符串按类模板解析成对象
     *
     * @param json 需要解析的json字符串
     * @param cls  类模板
     * @return 解析好的对象
     */
    public static <T> T getObj(String json, Class<T> cls) {
        Gson gson = new Gson();
        T bean = (T) gson.fromJson(json, cls);
        return bean;
    }
}
