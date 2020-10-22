package com.liuz.nio.pool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @className: Processor
 * @Author: liuzheng
 * @Date: 2020/10/22 15:29
 * @Description:
 */
@Slf4j
public class Processor {
    private static final ExecutorService SERVICE =
            Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors());
    private Selector selector;
    @SneakyThrows
    public Processor(){
        this.selector = SelectorProvider.provider().openSelector();
        start();
    }
    @SneakyThrows
    public void addChannel(SocketChannel socketChannel) {
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    public void wakeup() {
        this.selector.wakeup();
    }

    private void start() {
        SERVICE.submit(() ->{
            while (true) {
                if (selector.select(500) <= 0) {
                    continue;
                }
                for (Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator(); keyIterator.hasNext(); ) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if(key.isReadable()){
                        log.info("isReadable");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        int count = socketChannel.read(buffer);
                        if (count < 0) {
                            socketChannel.close();
                            key.cancel();
                            log.info("{}\t Read ended", socketChannel);
                            continue;
                        } else if (count == 0) {
                            log.info("{}\t Message size is 0", socketChannel);
                            continue;
                        } else {
                            write(buffer, socketChannel, count);
                        }
                    }
                }
            }
        });

    }

    public static void write(ByteBuffer buffer, SocketChannel socketChannel, int count) throws IOException {
        String respStr = new String(buffer.array(),0,count);

        log.info("{}\t Read message {}", socketChannel, respStr);

        ByteBuffer respBuffer = ByteBuffer.allocate(1024);

        if(respStr.equals("time")){
            String currentTime = "time".equalsIgnoreCase(respStr)?new Date(System.currentTimeMillis()).toString():"Bad Order";
            respBuffer.put(currentTime.getBytes());
            log.info("currentTime is {}",currentTime);
        }
        respBuffer.flip();
        log.info("{} write",socketChannel);
        socketChannel.write(respBuffer);
    }
}
