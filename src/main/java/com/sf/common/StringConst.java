package com.sf.common;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 22:37
 */
public interface StringConst {

    /**
     * TOKEN key prefix
     */
    String TOKEN_PREFIX = "TOKEN";
    /**
     * OBS 上传路径
     */
    String BASE_URL = "https://speakfreely.obs.cn-north-4.myhuaweicloud.com/";

    /**
     * redis key
     */
    String FILE_KEY = "FILES_FRONT_ALL";
    /**
     * 作为验证码获取的邮箱开头标记
     */
    String CODE_EMAIL = "codeEmail";

    /*checkUser操作类型*/
    /**
     * 操作为发送邮件
     */
    String SEND_EMAIL = "sendEmail";
    /**
     * 操作为用户注册
     */
    String USER_REGISTER = "userRegister";
    /**
     * 操作为用户名登录
     */
    String USERNAME_LOGIN = "usernameLogin";
    /**
     * 操作为邮箱登录
     */
    String EMAIL_LOGIN = "emailLogin";
    /**
     * 操作为密码重置
     */
    String PWD_RESET = "pwdReset";
    /**
     * 操作为邮箱换绑
     */
    String MODIFY_EMAIL = "modifyEmail";

    /*邮件发送操作*/
    /**
     * 邮件为注册邮件
     */
    String EMAIL_REGISTER = "emailRegister";
    /**
     * 邮件为换绑邮件
     */
    String EMAIL_MODIFY = "emailModify";
    /**
     * 邮件为密码重置邮件
     */
    String EMAIL_PWD_RESET = "emailPwdReset";
    /**
     * 邮件为信息修改邮件
     */
    String EMAIL_INFO_MODIFY = "emailInfoModify";
}
