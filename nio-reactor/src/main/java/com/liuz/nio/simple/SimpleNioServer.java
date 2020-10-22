package com.liuz.nio.simple;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

/**
 * @className: SimpleNioServer
 * @Author: liuzheng
 * @Date: 2020/10/21 19:09
 * @Description:
 */
@Slf4j
public class SimpleNioServer {

    @lombok.SneakyThrows
    public static void main(String[] args) {
        //创建一个NIO socket服务
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //将ServerSocketChannel设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //绑定服务端口
        serverSocketChannel.bind(new InetSocketAddress(9000));
        //调用 Selector.open()方法创建一个selector
        Selector selector = Selector.open();
        //将serverSocketChannel注册到selector中，监听TCP连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            //阻塞线程 等待客户端接入
            int nKey = selector.select();
            log.info("nkey:" + nKey);
            if(nKey>0){
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                int i = 0;
                while (keyIterator.hasNext()){
                    i++;
                    log.info("iteratorLength" + i);
                    SelectionKey key = keyIterator.next();
                    //移除
                    keyIterator.remove();
                    reactor(key,selector);

                }
            }
        }
    }

    private static void reactor(SelectionKey key,Selector selector){
        if(key.isAcceptable()){
            handlerAccept(key,selector);
        } else if(key.isReadable()){
            handlerReader(key);
        }
    }

    @SneakyThrows
    private static void handlerReader(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int n = 0;

        try {
            n = socketChannel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            socketChannel.socket().close();
            socketChannel.close();
            return;
        }
        log.info("读取数据"+n);
        if(n > 0){
            byte[] data = buffer.array();
            ByteBuffer respBuffer = ByteBuffer.allocate(1024);
            String respStr = new String(data,0 ,n);
            log.info("服务收到的信息：" + respStr);
            if(respStr.equals("time")){
                String currentTime = "time".equalsIgnoreCase(respStr)?new Date(System.currentTimeMillis()).toString():"Bad Order";
                respBuffer.put(currentTime.getBytes());
            }
            respBuffer.flip();
            socketChannel.write(respBuffer);
        }else{
            log.info("client is close");
            key.cancel();
        }

    }

    @SneakyThrows
    private static void handlerAccept(SelectionKey key, Selector selector) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        log.info("客户端连接："+ socketChannel.toString());
        //将通道注册到selector中
        //此时Selector监听channel并对read感兴趣
        socketChannel.register(selector,SelectionKey.OP_READ);
    }
}
