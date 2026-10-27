package com.medical.drugdelivery.config;

import com.medical.drugdelivery.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT登录拦截器
 * 除了登录、注册接口，其他请求都要先过这一关
 * 校验请求头里的token，不通过就直接返回401
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 在Controller执行之前先做token校验
     * 返回true才放行，返回false请求就到此为止了
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 先从请求头里拿token，没带或者前缀不对直接打回
        String header = request.getHeader(jwtUtil.getHeader());
        if (header == null || !header.startsWith(jwtUtil.getPrefix())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
            return false;
        }

        // 去掉"Bearer "前缀，剩下的才是真正的token
        String token = header.substring(jwtUtil.getPrefix().length()).trim();
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // token没问题，把用户信息塞到request里，Controller里直接getAttribute就能拿到
        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsername(token));
        request.setAttribute("roleId", jwtUtil.getRoleId(token));

        return true;
    }
}
