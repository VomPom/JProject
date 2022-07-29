package wang.julis.learncpp.ops;


import wang.julis.learncpp.BaseOperation;
import wang.julis.learncpp.common.LogUtil;
import wang.julis.learncpp.model.Animal;


public class CacheFieldAndMethodOps extends BaseOperation {

    static {
        initCacheMethodId();
    }

    @Override
    public void invoke() {
        Animal animal = new Animal("Cache");

        staticCacheField(animal);
        LogUtil.INSTANCE.e("name is " + animal.getName());

        callCacheMethod(animal);
    }

    private native void staticCacheField(Animal animal);

    private native void callCacheMethod(Animal animal);

    private static native void initCacheMethodId();
}
