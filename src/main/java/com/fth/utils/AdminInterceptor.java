package com.fth.utils;

import com.fth.properties.JwtProperty;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.fth.constant.JwtConstant.ADMIN_ID;
@Component
@Slf4j

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
            Integer userId= (Integer) claims.get(ADMIN_ID);
            UserHolder.saveUser(userId);
        }catch (Exception e){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            // 3. 返回标准JSON错误信息
            response.getWriter().write("{\"code\":401,\"msg\":\"Token缺失或无效\"}");
            return false;
        }
        log.info("管理员放行");
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
