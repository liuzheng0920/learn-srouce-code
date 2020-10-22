package com.liuz.nio.simple;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * @className: SimpleNioClient
 * @Author: liuzheng
 * @Date: 2020/10/22 08:54
 * @Description:
 */
@Slf4j
public class SimpleNioClient implements Runnable{
    String host;
    int prot;
    Selector selector;
    SocketChannel socketChannel;
    volatile boolean stop;
    @SneakyThrows
    public SimpleNioClient(String host, int prot){
        this.host = host;
        this.prot = prot;
        //构建 socketChannel 对象
        socketChannel = SocketChannel.open();
        //设置非阻塞模式
        socketChannel.configureBlocking(false);
        //构建 selector 对象
        selector = Selector.open();
    }


    @SneakyThrows
    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            selector.select(1000);
            for (Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator(); keyIterator.hasNext();){
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                handlerInput(key);
            }
        }
    }

    @SneakyThrows
    private void handlerInput(SelectionKey key) {
        if(key.isValid()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if(key.isConnectable()){
                if(socketChannel.finishConnect()){
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    doWrite(socketChannel);
                }
            }else if(key.isReadable()){
                log.info("isReadable");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int num = socketChannel.read(buffer);
                if(num > 0){
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String out = new String(bytes,"utf-8");
                    log.info("Now is :" + out);
                    this.stop = true;
                }
            }
        }
    }

    private void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(this.host,this.prot))){
            //如果连接成功，则将SocketChannel注册到复用器上，监听read事件，并且发送请求数据
            doWrite(socketChannel);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else {
            //如果没有连接成功，则说明服务端没有返回tcp握手信息，但并不代表连接失败，只需要将SocketChannel注册到
            //Selector上，监听connect事件，当服务端返回syn-acy消息后，Selector就能轮询到这个SocketChannel出于连接就绪了
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] request = "time".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(request.length);
        buffer.put(request);
        buffer.flip();
        socketChannel.write(buffer);
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(new SimpleNioClient("127.0.0.1",9000));

    }
}
