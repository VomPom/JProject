package wang.julis.jproject.example.thread

import android.content.Context
import wang.julis.jwbase.basecompact.IBaseTest
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


/**
 *
 * Created by @juliswang on 2025/04/08 16:06
 *
 * @Description
 */
object ReentrantLockTest : IBaseTest() {

    /**
     * ReentrantLock重入锁，是实现Lock接口的一个类
     *
     * 支持重入性，表示能够对共享资源能够重复加锁，即当前线程获取该锁再次获取不会被阻塞
     * ReentrantLock还支持公平锁和非公平锁两种方式
     *
     * 何谓公平性?
     * 是针对获取锁而言的，如果一个锁是公平的，那么锁的获取顺序就应该符合请求上的绝对时间顺序，满足FIFO。
     *
     * @param context Context
     */

    override fun run(context: Context) {
//        case1()
//        case2()
//        case3()
//        case4()
//        case5()
    }


    class Counter {
        private val lock = ReentrantLock()
        private var count = 0

        /**
         * 场景1：同步执行（同synchronized）
         */
        fun increment() {
            lock.lock() // 获取锁
            try {
                count++
                println("Count incremented to: $count")
            } finally {
                lock.unlock() // 确保锁被释放
            }
        }

        /**
         * case2 尝试等待执行
         * 如果排队等待时间过长，则舍弃对应的任务
         */
        fun tryAndWait() {
            try {
                if (lock.tryLock(2, TimeUnit.SECONDS)) {
                    // 模拟操作
                    try {
                        println(Thread.currentThread().name + " do work")
                        count++
                        TimeUnit.SECONDS.sleep(3)       // 第一次成功执行，sleep 3s，下一次任务只尝试了 2s 的等待，那么则是吧
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } finally {
                        lock.unlock()
                    }
                } else {
                    println(Thread.currentThread().name + " abort, count:$count")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * case3 尝试执行
         * 发现有线程已经在执行了，就直接放弃执行
         */
        fun tryAndAbort() {
            // 如果已经被lock，则立即返回false不会等待，达到忽略操作的效果
            if (lock.tryLock()) {
                try {
                    // 模拟操作
                    println(Thread.currentThread().name + " get lock")
                    try {
                        TimeUnit.SECONDS.sleep(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                } finally {
                    lock.unlock()
                }
            } else {
                println(Thread.currentThread().name + " give up this task.")
            }
        }

        /**
         * case4: 可中断执行
         *
         * 发现有线程在执行，等待，当正在进行的操作发生中断时，释放锁，进行下一个操作
         *
         * lockInterruptibly()方法能够中断等待获取锁的线程。
         * 当两个线程同时通过lock.lockInterruptibly() 获取某个锁时，假若此时线程A获取到了锁，而线程B只有等待，
         * 那么对线程B调用threadB.interrupt()方法能够中断线程B的等待过程。
         *
         *
         */
        fun lockInterruptibility() {
            try {
                lock.lockInterruptibly()
                try {
                    // t3线程报中断
                    if ("t3" == Thread.currentThread().name) {
                        throw InterruptedException()
                    }
                    println(Thread.currentThread().name + " do work")
                    TimeUnit.SECONDS.sleep(1)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                lock.unlock()
            }
        }

        fun reEntrance() {
            // 可重入锁；注意点 lock(), unlock()配对使用
            Thread({
                lock.lock()
                try {
                    println(Thread.currentThread().name + "=====外层=====")
                    lock.lock()
                    try {
                        println(Thread.currentThread().name + "=====内层=====")
                    } finally {
                        lock.unlock()
                    }
                } finally {
                    lock.unlock()
                }
            }, "t1").start()

            Thread({
                lock.lock()
                try {
                    println(Thread.currentThread().name + "=== 调用")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    lock.unlock()
                }
            }, "t2").start()
        }
    }


    private fun case1() {
        val counter = Counter()
        // 创建多个线程访问共享资源
        for (i in 0..10) {
            Thread {
                counter.increment()
                Thread.sleep(1000)
            }.start()
        }
    }

    private fun case2() {
        val counter = Counter()
        for (i in 0..10) {
            Thread({
                counter.tryAndWait()
            }, "thread t$i").start()
        }
    }

    private fun case3() {
        val counter = Counter()
        for (i in 0..10) {
            Thread({
                counter.tryAndAbort()
            }, "thread t$i").start()
        }
    }

    private fun case4() {
        val counter = Counter()
        for (i in 0..10) {
            Thread({
                counter.lockInterruptibility()
            }, "t$i").start()
        }
    }

    private fun case5() {
        val counter = Counter()
        counter.reEntrance()
    }


}

