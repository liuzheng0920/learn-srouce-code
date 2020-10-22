package com.liuz.nio;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @className: NioSelectorClient
 * @Author: liuzheng
 * @Date: 2020/10/21 11:05
 * @Description:客户端
 */
public class NioSelectorClient {

    @SneakyThrows
    public static void main(String[] args) {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(9000));
    }
}
