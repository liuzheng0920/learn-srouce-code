package com.liuz.nio.multi;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @className: Processor
 * @Author: liuzheng
 * @Date: 2020/10/22 14:09
 * @Description:
 */
@Slf4j
public class Processor {
    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(16);

    public void processor(SelectionKey key) {
        SERVICE.execute(()->{
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel socketChannel = (SocketChannel) key.channel();
                int count = socketChannel.read(buffer);
                if(count < 0){
                    socketChannel.close();
                    key.cancel();
                    log.info("{}\t Read ended", socketChannel);
                    return ;
                } else if(count == 0){
                    return ;
                }
                com.liuz.nio.pool.Processor.write(buffer, socketChannel, count);
                return ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
