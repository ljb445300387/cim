package com.crossoverjie.cim.common.pojo;

/**
 * Function: 用户信息
 *
 * @author crossoverJie
 *         Date: 2018/12/24 02:33
 * @since JDK 1.8
 */
public class CimUserInfo {
    private Long userId ;
    private String userName ;

    public CimUserInfo(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "CimUserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
