package com.bupt.dailyhaha.anno;

import java.lang.annotation.*;


/**
 * @author wangz
 * 用来做权限校验的注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AuthRequired {
}
