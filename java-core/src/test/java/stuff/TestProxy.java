package stuff;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TestProxy {

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
    static class HandlerSet<T> implements InvocationHandler {
        private final Set<T> original;

        public HandlerSet() {
            this.original = new TreeSet<>();
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            System.out.println("BEFORE "+method.getName());
            return method.invoke(original, args);
        }
    }

    public static void main(String[] args){
//        Original original = new Original();
//        Handler handler = new Handler(original);
//        If f = (If) Proxy.newProxyInstance(If.class.getClassLoader(),
//                new Class[] { If.class },
//                handler);
//        f.originalMethod("Hallo");

        HandlerSet<Integer> hset = new HandlerSet<>();
        Set<Integer> proxSet = (Set<Integer>) Proxy.newProxyInstance(Set.class.getClassLoader(), new Class[]{Set.class}, hset);
        proxSet.add(10);
        proxSet.add(15);
        proxSet.remove(15);

    }

}