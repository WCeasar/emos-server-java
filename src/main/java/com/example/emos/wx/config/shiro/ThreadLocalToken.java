package com.example.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

@Component
public class ThreadLocalToken {
    private  ThreadLocal<String> local = new ThreadLocal<>();

    public String getToken(){
        return local.get();
    }

    public void setToken(String token){
        local.set(token);
    }

    public void clear(){
        local.remove();
    };

}
