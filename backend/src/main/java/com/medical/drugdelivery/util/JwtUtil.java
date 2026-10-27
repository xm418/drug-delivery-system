package com.medical.drugdelivery.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 负责token的生成、解析和校验，登录成功后就是靠它发token给前端的
 * 秘钥、过期时间（配的24小时）这些都写在application.yml里
 */
@Component
public class JwtUtil {

    /** 签名秘钥，从配置文件读 */
    @Value("${jwt.secret}")
    private String secret;

    /** token有效期，单位毫秒，配的是24小时 */
    @Value("${jwt.expire}")
    private Long expire;

    /** 前端传token用的请求头名字，一般是Authorization */
    @Value("${jwt.header}")
    private String header;

    /** token的前缀，一般是"Bearer " */
    @Value("${jwt.prefix}")
    private String prefix;

    /**
     * 根据秘钥生成签名用的Key
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     * 把用户id、用户名、角色id塞进token里，后面拦截器解析出来就能直接用
     */
    public String generateToken(Long userId, String username, Integer roleId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleId", roleId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())  // 签发时间
                // 过期时间 = 当前时间 + 配置的24小时
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析token
     * 解析失败（签名不对、过期、格式错误等）统一返回null，不往外抛异常
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // token有问题就返回null，让调用方自己判断
            return null;
        }
    }

    /**
     * 校验token是否有效
     * 能解析出来并且还没过期就算有效
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && claims.getExpiration().after(new Date());
    }

    /**
     * 从token里取用户id
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
        }
        return null;
    }

    /**
     * 从token里取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims != null ? (String) claims.get("username") : null;
    }

    /**
     * 从token里取角色id
     */
    public Integer getRoleId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object roleId = claims.get("roleId");
            if (roleId != null) {
                return Integer.valueOf(roleId.toString());
            }
        }
        return null;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }
}
