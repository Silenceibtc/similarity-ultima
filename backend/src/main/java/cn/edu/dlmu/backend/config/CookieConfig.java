package cn.edu.dlmu.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 跨网站跨域Cookie配置
 * 说明：如果前后端不是部署在同一机器上，不配置会出现前端不能set-cookie，每次请求都产生一个新的会话
 * 相关文章：<a href="https://cloud.tencent.com/developer/article/2207110"></a>
 */
@Configuration
public class CookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookiePath("/");                      // 作用所有路径
        serializer.setUseHttpOnlyCookie(true);              // 启用 HttpOnly
        serializer.setUseSecureCookie(true);                // 仅 HTTPS 生效
        serializer.setSameSite("None");                     // 允许跨站 AJAX 携带
        return serializer;
    }
}