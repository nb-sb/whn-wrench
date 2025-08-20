package cn.web3er.wrench.trigger;

import cn.web3er.wrench.rate.limiter.enums.LimitType;
import cn.web3er.wrench.rate.limiter.types.annotations.RateLimiter;
import cn.web3er.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexController {

    /**
     * curl --request GET \
     *   --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=hhh'
     */
    @RateLimiterAccessInterceptor(key = "userId", fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1.0d, blacklistCount = 1)
    @RequestMapping(value = "draw", method = RequestMethod.GET)
    public String draw(String userId) {
        return "test";
    }



    /**
     * 自定义限流策略：30秒内最多允许5次访问
     */
    @GetMapping("/test2")
    @RateLimiter(key = "test2", time = 30, count = 5, message = "访问过于频繁，请稍后再试")
    public String test2() {
        return "success";
    }

    /**
     * 基于IP的限流策略
     */
    @GetMapping("/test3")
    @RateLimiter(key = "test3", limitType = LimitType.IP, time = 60, count = 10,fallbackMethod = "log")
    public String test3() {
        return "success";
    }

    /**
     * 集群限流策略，带有自定义回调方法
     */
    @GetMapping("/test4/{userId}")
    @RateLimiter(key = "userId", limitType = LimitType.CLUSTER, time = 5, count = 2,fallbackMethod = "drawErrorRateLimiter")
    public String test4(@PathVariable("userId") String userId) {
        return "success: " + userId;
    }
    /**
     * 测试请求IP限流(key基于参数获取)
     * 同一IP请求受影响
     *
     * 简单变量获取 #变量 复杂表达式 #{#变量 != 1 ? 1 : 0}
     */
    @RateLimiter(count = 2, time = 10, limitType = LimitType.IP, key = "#value")
    @GetMapping("/testObj")
    public String testObj(String value) {
        return value;
    }

    public String drawErrorRateLimiter(String userId) {
        System.out.println("限流了 this is " + userId);
        return "rateLimiter";
    }
    public String log() {
        System.out.println("限流了 this is log" );
        return "rateLimiter";
    }

}
