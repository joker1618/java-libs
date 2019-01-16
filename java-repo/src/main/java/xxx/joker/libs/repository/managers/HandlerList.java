package xxx.joker.libs.repository.managers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class HandlerList implements InvocationHandler {

    private final RepoDataHandler repoHandler;
    private final ArrayList<Object> sourceList;

    private HandlerList(RepoDataHandler repoHandler, Collection<? extends Object> data) {
        this.repoHandler = repoHandler;
        this.sourceList = new ArrayList<>(data);
    }

    public static List<Object> createProxyList(RepoDataHandler repoDataHandler) {
        return createProxyList(repoDataHandler, Collections.emptyList());
    }
    public static List<Object> createProxyList(RepoDataHandler repoDataHandler, Collection<? extends Object> data) {
        HandlerList handler = new HandlerList(repoDataHandler, data);
        ClassLoader loader = ArrayList.class.getClassLoader();
        Class[] interfaces = {List.class};
        return (List<Object>) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // todo impl
        synchronized (sourceList) {
            if ("add".equals(method.getName())) {


            } else if ("addAll".equals(method.getName())) {

            } else if ("remove".equals(method.getName())) {

            } else if ("removeIf".equals(method.getName())) {

            } else if ("removeAll".equals(method.getName())) {

            } else if ("clear".equals(method.getName())) {

            }
        }

        return null;
    }
}
