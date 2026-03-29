package txu.report.mainapp.service;

//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.context.event.EventListener;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import txu.report.mainapp.event.DepartmentLoadedEvent;
//
//import java.time.Duration;

//@Service
//@ConditionalOnBean(RedisTemplate.class)
//@Slf4j
public class RedisWarmService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public RedisWarmService(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Async
//    @EventListener
//    public void onDepartmentLoaded(DepartmentLoadedEvent event) {
//        try {
//            redisTemplate.opsForValue().set(
//                    "department:" + event.id(),
//                    event.dept(),
//                    Duration.ofMinutes(5)
//            );
//        } catch (Exception e) {
//            log.warn("Redis warm failed – ignored");
//        }
//    }
}