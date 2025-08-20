package cn.web3er.wrench.rate.limiter.config;


import cn.web3er.wrench.rate.limiter.aop.RateLimiterAOP;
import cn.web3er.wrench.rate.limiter.aop.RateLimiterAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAutoConfig {

    @Bean
    public RateLimiterAOP rateLimiterAOP() {
        return new RateLimiterAOP();
    }
    @Bean
    public RateLimiterAspect rateLimiterAspect() {
        return new RateLimiterAspect();
    }
}
