package cn.edu.dlmu.backend.scheduled;

import cn.edu.dlmu.backend.constant.RedisKey;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PreheatCache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;


    List<Long> userIdList = Arrays.asList(9L);

    @Scheduled(cron = "0 0 0 * * ?")
    public void doPreheatCache() {
        RLock lock = redissonClient.getLock(RedisKey.PREHEAT_CACHE_LOCK_KEY);
        try {
            // 只有一个线程能拿到锁
            if (lock.tryLock(0, -1, TimeUnit.SECONDS)) {
                for (Long userId : userIdList) {
                    String redisKey = String.format(RedisKey.USER_HOMEPAGE_REDIS_KEY, 1, 10, userId);
                    // 查询用户信息
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    List<User> userList = userService.page(new Page<>(1, 10), queryWrapper).getRecords();
                    // 用户信息脱敏
                    List<User> safeUserList = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
                    // 预热缓存
                    try {
                        redisTemplate.opsForValue().set(redisKey, safeUserList, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("Redis缓存写入失败，键: {}，错误信息: {}", redisKey, e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("缓存预热任务执行失败，错误信息: {}", e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
