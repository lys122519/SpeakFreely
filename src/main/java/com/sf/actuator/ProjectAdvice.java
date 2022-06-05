package com.sf.actuator;

import cn.hutool.json.JSONObject;
import com.sf.common.Constants;
import com.sf.common.StringConst;
import com.sf.utils.RedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-04 21:36
 */
@Component
@Aspect
public class ProjectAdvice {

    private static final Logger log = LoggerFactory.getLogger(ProjectAdvice.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    ConcurrentHashMap<Object, Object> countMap = new ConcurrentHashMap<Object, Object>();

    /**
     * 匹配控制层层通知
     */
    @Pointcut("execution(* com.sf.controller.*Controller.*(..))")
    private void servicePt() {

    }


    /**
     * 在接口原有的方法执行前，将会首先执行此处的代码
     */
    @Before("ProjectAdvice.servicePt()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

        startTime.set(System.currentTimeMillis());
        //获取传入目标方法的参数
        Object[] args = joinPoint.getArgs();


        //if (countMap.size() == 0) {
        //
        //    Map<Object, Object> objectObjectMap = RedisUtils.mapFromRedis(StringConst.INTERFACE_ACTUATOR);
        //    objectObjectMap.
        //
        //    //countMap = (ConcurrentHashMap<Object, Object>) redisTemplate.opsForHash().entries(StringConst.INTERFACE_ACTUATOR);
        //}

        //log.info("类名：{}", joinPoint.getSignature().getDeclaringType().getSimpleName());
        //log.info("方法名:{}", joinPoint.getSignature().getName());

    }


    /**
     * 只有正常返回才会执行此方法
     * 如果程序执行失败，则不执行此方法
     */
    @AfterReturning(returning = "returnVal", pointcut = "ProjectAdvice.servicePt()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnVal) throws Throwable {

        Signature signature = joinPoint.getSignature();
        String declaringName = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String mapKey = declaringName + methodName;

        // 执行成功则计数加一
        int increase = AtomicCounter.getInstance().increase();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();


        if (countMap.size() == 0) {
            JSONObject jsonObject = RedisUtils.objFromRedis(StringConst.INTERFACE_ACTUATOR);
            if (jsonObject != null) {
                synchronized (this) {
                    Set<String> strings = jsonObject.keySet();
                    for (String string : strings) {
                        Object o = jsonObject.get(string);
                        countMap.putIfAbsent(string, o);
                    }
                }
            }
        }

        //   如果不存在 放入
        countMap.putIfAbsent(mapKey, 0);
        countMap.compute(mapKey, (key, value) -> (Integer) value + 1);

        // 内存计数达到100 更新redis
        if (increase == Constants.INTERFACE_COUNT) {
            synchronized (this) {

                RedisUtils.objToRedis(StringConst.INTERFACE_ACTUATOR, countMap, Constants.AVA_REDIS_TIMEOUT);
                //删除过期时间
                stringRedisTemplate.persist(StringConst.INTERFACE_ACTUATOR);
                AtomicCounter.getInstance().toZero();
            }
        }


        //log.info("方法执行次数:" + mapKey + "------>" + countMap.get(mapKey));
        //log.info("URI:[{}], 耗费时间:[{}] ms", request.getRequestURI(), System.currentTimeMillis() - startTime.get());
    }


    /**
     * 当接口报错时执行此方法
     */
    @AfterThrowing(pointcut = "ProjectAdvice.servicePt()")
    public void doAfterThrowing(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.info("接口访问失败，URI:[{}], 耗费时间:[{}] ms", request.getRequestURI(), System.currentTimeMillis() - startTime.get());
    }

}
