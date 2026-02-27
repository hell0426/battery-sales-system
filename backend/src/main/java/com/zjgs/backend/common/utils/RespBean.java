package com.zjgs.backend.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

//接口规范类 - 返回结果
@Data
@Accessors(chain = true)
public class RespBean {
    //是否成功
    private Boolean success;

    //状态码
    private Integer code;

    //提示信息
    private String msg;

    //返回数据
    private Map<String, Object> data =new HashMap<>();

    //成功方法
    public static RespBean ok(){
        return new RespBean().setSuccess(true).setCode(200).setMsg("成功");
    }
    //成功方法
    public static RespBean err(){
        return new RespBean().setSuccess(false).setCode(201).setMsg("失败");
    }

    //只返回状态码
    public RespBean code(Integer code){
        this.code = code;
        return this;
    }
    //只返回提示信息
    public RespBean msg(String msg){
        this.msg = msg;
        return this;
    }
    //只返回数据
    public RespBean data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public RespBean data(Map<String,Object> map){
        this.data.putAll(map);
        return this;
    }




}
