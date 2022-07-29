package wang.julis.learncpp.model;


import wang.julis.learncpp.common.LogUtil;
/**
 * Created by juliswang on 2022/7/29 18:42
 *
 * Description :
 *
 *
 */
public class Animal {


    protected String name;

    public static int num = 0;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getNum() {
        return num;
    }

    // C++ 调用 Java 的实例方法
    public void callInstanceMethod(int num) {
        LogUtil.INSTANCE.d("call instance method and num is " + num);
    }

    // C++ 调用 Java 的类方法
    public static String callStaticMethod(String str) {
        if (str != null) {
            LogUtil.INSTANCE.d("call static method with " + str);
        } else {
            LogUtil.INSTANCE.d("call static method str is null");
        }
        return "";
    }

    public static String callStaticMethod(String[] strs, int num) {
        LogUtil.INSTANCE.d("call static method with string array");
        if (strs != null) {
            for (String str : strs) {
                LogUtil.INSTANCE.d("str in array is " + str);
            }
        }
        return "";
    }

    public static void callStaticVoidMethod() {
        LogUtil.INSTANCE.d("call static void method");
    }
}
