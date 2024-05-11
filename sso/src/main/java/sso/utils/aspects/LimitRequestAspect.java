package sso.utils.aspects;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Aspect
@Configuration
@Component
public class LimitRequestAspect {

    Environment env;
    private final Bucket bucket;


    @Autowired
    public LimitRequestAspect(Environment env) {
        this.env = env;

        Long maxRequests = Long.parseLong(env.getProperty("security.requestratelimit"));
        Bandwidth bandwidthLimit = Bandwidth.classic(maxRequests, Refill.greedy(maxRequests, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
            .addLimit(bandwidthLimit)
            .build();
    }

    @Around("@annotation(sso.utils.annotations.LimitRequests)")
    public Object limitRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }

        return joinPoint.proceed();
    }

}
