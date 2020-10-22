package com.liuz.nio.pool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * @className: MultiReactor
 * @Author: liuzheng
 * @Date: 2020/10/22 15:08
 * @Description:
 */
@Slf4j
public class MultiReactor {
    @SneakyThrows
    public static void main(String[] args) {
        Selector selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9000));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        int coreNum = Runtime.getRuntime().availableProcessors();
        Processor[] processors = new Processor[2 * coreNum];
        for (int i = 0; i < processors.length ; i++){
            processors[i] = new Processor();
        }

        int index = 0;
        while (selector.select()>0){
            for(Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();keyIterator.hasNext();){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if(key.isAcceptable()){
                    ServerSocketChannel acceptServerSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = acceptServerSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    log.info("Accept request from {}",socketChannel);
                    Processor processor = processors[(int) ((index++) % coreNum)];
                    processor.addChannel(socketChannel);
                    processor.wakeup();

                }
            }
        }

    }
}
