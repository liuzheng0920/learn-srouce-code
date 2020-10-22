package com.liuz.nio;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * @className: NioSelectorServer
 * @Author: liuzheng
 * @Date: 2020/10/21 10:30
 * @Description: Nio服务
 */
@Slf4j
public class NioSelectorServer {

    /**
     * 启动服务
     */
    @SneakyThrows
    public static void main(String[] args) {
        //创建选择器
        Selector selector = Selector.open();
        //打开socket 服务 通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定一个端口
        serverSocketChannel.bind(new InetSocketAddress(9000));
        //设定non-blocking的方式。
        serverSocketChannel.configureBlocking(false);
        //向Selector组册Channel事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            //选择事件
            selector.select();
            for (Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();keyIterator.hasNext();){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                log.info(String.valueOf(key.interestOps()));
                if(key.isAcceptable()){
                    serverSocketChannel.accept();
                }
            }
        }


    }
}
