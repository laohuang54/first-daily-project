package com.fth.utils;

import com.fth.properties.JwtProperty;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.fth.constant.JwtConstant.ADMIN_ID;
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperty jwtProperty;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.equals("")) {
            response.getWriter().write("请先登录");
            return false;
        }
        try{
            Claims claims = JwtUtil.parseJWT(jwtProperty.getAdminSecretKey(), token);
            Long userId=Long.valueOf(claims.get(ADMIN_ID).toString());
            UserHolder.saveUser(userId);
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
