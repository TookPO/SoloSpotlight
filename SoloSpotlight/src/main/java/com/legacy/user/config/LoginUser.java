package com.legacy.user.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 메소드의 파라메터에 선언된 것만
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser { // 어노테이션 생성
}
