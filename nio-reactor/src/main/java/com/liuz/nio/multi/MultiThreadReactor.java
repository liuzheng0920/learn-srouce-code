package com.liuz.nio.multi;

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
 * @className: MultiThreadReator
 * @Author: liuzheng
 * @Date: 2020/10/22 13:58
 * @Description: 多线程 reactor
 */
@Slf4j
public class MultiThreadReactor {
    @SneakyThrows
    public static void main(String[] args) {
        Selector selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9000));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("启动成功");
        while (true){
            if(selector.selectNow()<0){
                continue;
            }
            for (Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();keyIterator.hasNext();){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if(key.isAcceptable()){
                    ServerSocketChannel acceptServerSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = acceptServerSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    log.info("Accept request from {}", socketChannel.getRemoteAddress());
                    SelectionKey readKey = socketChannel.register(selector,SelectionKey.OP_READ);
                    readKey.attach(new Processor());
                } else if (key.isReadable()){
                    Processor processor = (Processor) key.attachment();
                    processor.processor(key);
                }
            }
        }

    }
}
