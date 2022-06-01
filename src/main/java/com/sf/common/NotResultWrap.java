package com.sf.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * @Description:
 * @author: leung
 * @date: 2022-06-01 15:45
 */

@Target({METHOD})
@Retention(RUNTIME)
public @interface NotResultWrap {
}
