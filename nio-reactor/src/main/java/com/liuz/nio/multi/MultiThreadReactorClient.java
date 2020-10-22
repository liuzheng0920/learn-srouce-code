package com.liuz.nio.multi;

import com.liuz.nio.simple.SimpleNioClient;
import lombok.SneakyThrows;
import org.springframework.expression.spel.ast.Selection;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @className: Mult
 * @Author: liuzheng
 * @Date: 2020/10/22 14:20
 * @Description:
 */
public class MultiThreadReactorClient {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new SimpleNioClient("127.0.0.1",9000));

    }
}
