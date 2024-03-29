package com.sf.entity.dto;


import com.sf.enums.SexEnum;
import com.sf.enums.UserStateEnum;
import io.swagger.annotations.ApiModelProperty;

public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private SexEnum sex;
    private String address;
    private String avatarUrl;
    private String role;
    private String code;
    private String token;
    private String userFace;
    private UserStateEnum disabled;


    public UserStateEnum getDisabled() {
        return disabled;
    }

    public void setDisabled(UserStateEnum disabled) {
        this.disabled = disabled;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }
}
