package wang.julis.learncpp.ops;

import wang.julis.learncpp.BaseOperation;
import wang.julis.learncpp.common.LogUtil;


public class ThreadOps extends BaseOperation {

    @Override
    public void invoke() {
        simpleNativeThread();
        nativeInit();
        posixThreads(3, 3);
    }

    private native void simpleNativeThread();

    private native void nativeInit();

    private native void nativeFree();

    private native void posixThreads(int threads, int iterations);

    /**
     * 打印线程名称，并且模拟耗时任务
     */
    private void printThreadName() {
        LogUtil.INSTANCE.e("print thread name current thread name is " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Native 回到到 Java 的方法，打印当前线程名字
     *
     * @param msg
     */
    private void printNativeMsg(String msg) {
        LogUtil.INSTANCE.e("native msg is " + msg);
        LogUtil.INSTANCE.e("print native msg current thread name is " + Thread.currentThread().getName());
    }
}
