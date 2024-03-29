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
     * 接口监控key
     */
    String INTERFACE_ACTUATOR = "INTERFACE_ACTUATOR";


    /**
     * 活跃用户key
     */
    String ACTIVE_USER = "ACTIVE_USER";

    /**
     * 管理员role
     */
    String USER_ADMIN = "ROLE_ADMIN";

    /**
     * 百度AI智能云Token
     */
    String BAIDU_ACCESS_TOKEN = "24.e0cf8ab9a9b3ab0aec711f4c6b59105a.2592000.1657177329.282335-26370056";

    /*---redis中的缓存key---*/

    /*1.单个对象以String缓存*/
    /*存的时候key为其唯一标识字符串，值为该对象转Json对象再转字符串*/
    /*取的时候要将值转为JsonObject即可访问其属性值*/
    /*每个对象key都不一样，但可以总结为以下几种情况*/
    /*1.1 某个User的缓存以token为key, 值为其json字符串*/

    /*2.一组对象以Map缓存*/
    /*存的时候是以key,Map<对象的唯一标识字符串, 对象转Json对象再转字符串>形式*/
    /*取的时候通过key得到的是一批对象的Map<Object, Object>形式*/
    /*这种对象有公共的key，先根据key取出一组Map<Object，Object>*/
    /*再根据其单个对象的唯一标识可以取出单个对象的json字符串形式*/

    /**
     * 2.1 TagTop100缓存(热度前100个Tags对象的Map缓存)
     * Map<标签id, Tags转JsonObject再转String>
     */
    String TAGS_REDIS_KEY = "tagsMapRedis";

    /**
     * 2.2 ArticleTop5缓存(访问量前5个Article对象的Map缓存)
     * Map<文章id, Article转JsonObject再转String>
     */
    String ARTICLE_REDIS_KEY = "articleMapRedis";

    /**
     * 2.3 用户id与token对应关系Map
     * Map<用户id, 用户token>
     */
    String USERID_TOKEN_REDIS_KEY = "userIDTokenMapRedis";


    /*3.一组对象以List缓存(实测同样的数据比Map方式占内存，且理论上字典结构比列表结构存取速度快)*/
    /*存的时候是以List<对象转Json对象再转字符串>形式*/
    /*取的时候通过key得到的是一批对象的List<JsonString>形式*/
    /*这种对象有公共的key，先根据key取出一组List<JsonString>*/
    /*再遍历列表将其由JsonString还原为JsonObject*/


    /*---redis中的缓存key结束---*/

    /*---UserServiceImpl相关操作---*/
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
     * 邮件为注销邮件
     */
    String EMAIL_LOGOUT = "emailLogout";
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
    /*---UserServiceImpl相关操作结束---*/

    /*---ArticleServiceImpl相关操作---*/

    /*articleAction方法action标识*/
    /**
     * 操作为存草稿
     */
    String ARTICLE_DRAFT = "draft";
    /**
     * 操作为文章发布
     */
    String ARTICLE_PUBLISH = "publish";

    /*---ArticleServiceImpl相关操作结束---*/
    /**
     * 邮件内容模板
     */
    String EMAIL_CONTENT = "<div class='main' style='padding-bottom:60px;background-color:#F1F1F1;'>" +
            "        <div class='header' style='padding: 50px 60px'>" +
            "             <h1 style='color:#fb5151;'>畅·言</h1>" +
            "     <h2 style='color:#fb5151;'>Speak·Freely</h2>" +
            "        </div>" +
            "        " +
            "        <div class='content' style='overflow: hidden;padding:30px 10% 70px 10%;margin:0 10%;background-color: #fff;box-shadow:0 4px 20px rgba(0,0,0,0.1);word-break: break-all;'>" +
            "         <h2 style='margin: 30px 0;'> 您好！</h2>" +
            "          <p style='margin-bottom: 40px;'>请使用下面的验证码验证您的操作，验证码 2 分钟内有效:</p>" +
            "          <span style='padding: 10px 20px; font-size: 24px;background-color: #EB6F5A;border-radius:4px;color:#fff;'>CODE</span>" +
            "        </div>" +
            "      </div>";
}
