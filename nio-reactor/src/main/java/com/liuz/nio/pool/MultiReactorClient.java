package com.liuz.nio.pool;

import com.liuz.nio.simple.SimpleNioClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @className: MultiReactorClient
 * @Author: liuzheng
 * @Date: 2020/10/22 15:38
 * @Description:
 */
public class MultiReactorClient {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new SimpleNioClient("127.0.0.1",9000));
    }
}
