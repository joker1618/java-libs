package stuff;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;


public class JdkProxyDemo {
    interface If {
        void originalMethod(String s);
    }
    static class Original implements If {
        public void originalMethod(String s) {
            System.out.println(s);
        }
    }
    static class Handler implements InvocationHandler {
        private final If original;
        public Handler(If original) {
            this.original = original;
        }
        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            System.out.println("BEFORE");
            method.invoke(original, args);
            System.out.println("AFTER");
            return null;
        }
    }

    @Test
    public void testProxy(){
        Original original = new Original();
        Handler handler = new Handler(original);
        If f = (If) Proxy.newProxyInstance(If.class.getClassLoader(),
                new Class[] { If.class },
                handler);
        f.originalMethod("Hallo");
    }

    @Test
    public void testProxyMap(){
        Map<String, Integer> map = new HashMap<>();
        HandlerMap handlerMap = new HandlerMap(map);
        Map<String, Integer> proxiedMap = (Map<String, Integer>) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, handlerMap);
        proxiedMap.put("fede", 23);
    }

    static class HandlerMap implements InvocationHandler {
        private final Map<String, Integer> origMap;

        public HandlerMap(Map<String, Integer> origMap) {
            this.origMap = origMap;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            System.out.println("BEFORE " + method.getName());
            method.invoke(origMap, args);
            System.out.println("AFTER");
            return null;
        }
    }

    @Test
    public void testProxyList(){
        List<String> list = new ArrayList<>();
        list.add("uno");
        HandlerList handlerList = new HandlerList(list);
        List<String> proxiedList = (List<String>) Proxy.newProxyInstance(List.class.getClassLoader(), new Class[]{List.class}, handlerList);
        list.add("due");
        proxiedList.add("p tre");

        display("%s", list);
        display("%s", proxiedList);

        list.remove(0);
        proxiedList.remove(0);
        display("\n\n%s", list);
        display("%s", proxiedList);


    }

    static class HandlerList implements InvocationHandler {
        private final List<String> origList;

        public HandlerList(List<String> origList) {
            this.origList = origList;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            System.out.println("BEFORE " + method.getName());
            Object res = method.invoke(origList, args);
            System.out.println("AFTER " + method.getName());
            return res;
        }
    }
}