package com.itschenxiang.arthasdemo.controller;

import com.itschenxiang.arthasdemo.common.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class IndexController {

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new BasicThreadFactory.Builder()
                        .namingPattern("watch-lambda-pool-%d")
                        .daemon(true)
                        .build());
    }

    @PostMapping("/watchLambda")
    public CommonResponse<String> watchLambda() {
        executorService.submit(() -> {
            log.info("watchLambda 1");
            executorService.submit(() -> {
                log.info("watchLambda 2");
                executorService.submit(() -> {
                    log.info("watchLambda 3");
                });
            });
        });
        return CommonResponse.success("Hello Lambda");
    }

}
