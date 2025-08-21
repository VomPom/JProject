package wang.julis.jproject.example.thread

import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
 *
 * Created by @juliswang on 2025/04/08 16:04
 *
 * @Description
 */
class ThreadMainActivity : BaseListActivity() {
    /**
     * 线程安全需要保证几个基本特性：
     *
     * 原子性，简单说就是相关操作不会中途被其他线程干扰，一般通过同步机制实现。
     * 可见性，是一个线程修改了某个共享变量，其状态能够立即被其他线程知晓，通常被解释为将线程本地状态反映到主内存上，volatile 就是负责保证可见性的。
     * 有序性，是保证线程内串行语义，避免指令重排等。
     *
     */
    override fun initData() {
        addItem("ReentrantLock") { ReentrantLockTest.run(this) }
    }

}