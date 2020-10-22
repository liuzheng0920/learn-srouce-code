## 初见 selector
### 1.selector 方法解析
#### 1.1 open() 创建一个通道

通过系统默认的要给选择器提供者（provider）或者自定义的provider创建一个新的选择器（selector）。

Selector selector = Selector.open();

注册可选择的通道：

为了让选择器监视任何通道，我们必须向选择器注册这些通道。我们通过调用可选通道的register方法来实现这一点。但是在通道注册到选择器之前，它必须处于非阻塞模式:

channel.configureBlocking(false);

SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);

第一个参数是我们前面创建的Selector对象，第二个参数通过选择器在监控通道中侦听哪些事件。

我们可以侦听四个不同的事件，每个事件都由SelectionKey类中的常量表示:

** 
Connect—当客户端试图连接到服务器时。SelectionKey.OP_CONNECT

Accept—服务器接受来自客户机的连接。SelectionKey.OP_ACCEPT。

Read—当服务器准备从通道读取数据时。SelectionKey.OP_READ。

Write—当服务器准备写入通道时。SelectionKey.OP_WRITE。
**
#### 1.2 select() 
在刚初始化的Selector对象中，这三个集合都是空的。 通过Selector的select（）方法可以选择已经准备就绪的通道 。比如你对读就绪的通道感兴趣，那么select（）方法就会返回读事件已经就绪的那些通道。

int select()：阻塞到至少有一个通道在你注册的事件上就绪了。

int select(long timeout)：和select()一样，最长阻塞时间为timeout毫秒。

int selectNow()：非阻塞，只要有通道就绪就立刻返回。

注：返回的键的数量，可能是0，已更新的就绪操作集数量。select()方法返回的int值表示有多少通道已经就绪,是自上次调用select()方法后有多少通道变成就绪状态。之前在select（）调用时进入就绪的通道不会在本次调用中被记入，而在前一次select（）调用进入就绪但现在已经不在处于就绪的通道也不会被记入。

#### 1.3 wakeup():唤醒在select（）方法中阻塞的线程

通过调用Selector对象的wakeup（）方法让处在阻塞状态的select()方法立刻返回 该方法使得选择器上的第一个还没有返回的选择操作立即返回。如果当前没有进行中的选择操作，那么下一次对select()方法的一次调用将立即返回。

#### 1.4 close() 

通过close（）方法关闭Selector， 该方法使得任何一个在选择操作中阻塞的线程都被唤醒（类似wakeup（）），同时使得注册到该Selector的所有Channel被注销，所有的键将被取消，但是Channel本身并不会关闭.

#### 1.5 selectedKeys() 访问已选择键集合

         





