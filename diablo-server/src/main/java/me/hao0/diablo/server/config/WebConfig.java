package me.hao0.diablo.server.config;

import me.hao0.diablo.server.interceptor.ClientInterceptor;
import me.hao0.diablo.server.interceptor.LocaleInterceptor;
import me.hao0.diablo.server.interceptor.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor());
        registry.addInterceptor(clientInterceptor());
        registry.addInterceptor(serverInterceptor());
    }

    @Bean
    public ClientInterceptor clientInterceptor() {
        return new ClientInterceptor();
    }

    @Bean
    public ServerInterceptor serverInterceptor() {
        return new ServerInterceptor();
    }

    @Bean
    public LocaleInterceptor localeInterceptor(){
        return new LocaleInterceptor();
    }
}