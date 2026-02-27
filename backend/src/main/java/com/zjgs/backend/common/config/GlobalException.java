package com.zjgs.backend.common.config;


import com.zjgs.backend.common.exception.MyException;
import com.zjgs.backend.common.utils.RespBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//控制器中如果出现了异常会被当前组件所拦截
@RestControllerAdvice
public class GlobalException {

    //运行异常拦截
    @ExceptionHandler(RuntimeException.class)
    public RespBean runtimeException(RuntimeException e) {
        return RespBean.err().msg("运行时异常："+e.getMessage());
    }

    //逻辑异常拦截
   @ExceptionHandler(ArithmeticException.class)
   public RespBean arithmeticException(ArithmeticException e) {
        return RespBean.err().msg("逻辑异常:" + e.getMessage());
   }

    //自定义异常拦截
    @ExceptionHandler(MyException.class)
    public RespBean myException(MyException e) {
        return RespBean.err().msg("自定义异常:" + e.getMessage());
    }
}
