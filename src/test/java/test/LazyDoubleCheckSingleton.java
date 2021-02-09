package test;

import java.io.Serializable;
import java.util.Date;

public class LazyDoubleCheckSingleton{
    private Date date = new Date();
    private volatile static LazyDoubleCheckSingleton INSTANCE = null;
    private LazyDoubleCheckSingleton(){

    }

    public static LazyDoubleCheckSingleton getInstance() {
        if (INSTANCE == null) {
            synchronized (LazyDoubleCheckSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LazyDoubleCheckSingleton();
                    // 1. 分配内存给这个对象
                    // 2. 初始化对象
                    // 3. 设置INSTANCE指向刚分配内存地址
                }
            }
        }
        return INSTANCE;
    }
}
