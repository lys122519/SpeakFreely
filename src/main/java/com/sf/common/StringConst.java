package com.sf.common;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 22:37
 */
public interface StringConst {
    /**
     * OBS 上传路径
     */
    String BASE_URL = "https://file-but.obs.cn-north-4.myhuaweicloud.com/";

    /**
     * redis key
     */
    String FILE_KEY = "FILES_FRONT_ALL";
    /**
     * 作为验证码获取的邮箱开头标记
     */
    String CODE_EMAIL = "codeEmail";
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
     * 操作为信息修改
     */
    String INFO_MODIFY = "infoModify";

}
