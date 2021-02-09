package club.codermax.rpc.register;

import club.codermax.rpc.framework.URL;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * 不需要加锁？？？？
 */
public class Register {
    private static Map<String, Map<URL, String>> register = new HashMap<>();

    public static void regist(URL url, String className, String impl) {
        Map<URL, String> map = new HashMap<>();
        map.put(url, impl);
        register.put(className, map);
        saveFile();
    }

    private static void saveFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("temp.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(objectOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String,Map<URL,String>> getFile(){
        try{
            FileInputStream fileInputStream = new FileInputStream("temp.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Map<String,Map<URL,String>>) objectInputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里使用迭代器的原因是什么？？？？
     * @param url
     * @param interfaceName
     * @return
     */
    public static URL get(URL url, String interfaceName) {
        register = getFile();
        return register.get(interfaceName).keySet().iterator().next();
    }
}
