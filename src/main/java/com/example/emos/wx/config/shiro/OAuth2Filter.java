package com.example.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ThreadLocalToken localToken;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.expire}")
    private int cacheExpire;


    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String token = getRequestToken(req);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    /**
     * options请求放开
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return false;
    }

    /**
     * 该方法用于处理所有应该被Shiro处理的请求
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        localToken.clear();
        String token = getRequestToken(req);

        if (StrUtil.isBlank(token)) {
            res.setStatus(HttpStatus.SC_UNAUTHORIZED);
            res.getWriter().print("无效的令牌");
            return false;
        };

        try {
            jwtUtil.verifierToken(token); // 检查令牌是否过期
        } catch (TokenExpiredException e) {
            // 客户端令牌过期, 查询Redis中是否存在令牌, 如果存在令牌就重新生成一个令牌给客户端
            if (redisTemplate.hasKey(token)) {
                redisTemplate.delete(token);
                int userId = jwtUtil.getUserId(token);
                token = jwtUtil.createToken(userId);
                redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
                // 把令牌绑定到线程
                localToken.setToken(token);
            } else {
                res.setStatus(HttpStatus.SC_UNAUTHORIZED);
                res.getWriter().print("令牌已经过期");
            }
            return false;
        } catch (JWTDecodeException e) {
            res.setStatus(HttpStatus.SC_UNAUTHORIZED);
            res.getWriter().print("无效的令牌");
            return false;
        }

        // 认证失败
        boolean bool = executeLogin(servletRequest, servletResponse);
        return bool;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        try {
            res.getWriter().print(e.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return false;
    }

    private String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        return token;
    }
}











































































