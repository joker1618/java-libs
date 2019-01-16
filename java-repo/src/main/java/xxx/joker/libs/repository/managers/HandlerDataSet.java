package xxx.joker.libs.repository.managers;

import xxx.joker.libs.repository.design.JkEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class HandlerDataSet implements InvocationHandler {

    private final RepoDataHandler repoHandler;
    private final TreeSet<JkEntity> dataSet;

    private HandlerDataSet(RepoDataHandler repoHandler, Collection<JkEntity> data) {
        this.repoHandler = repoHandler;
        this.dataSet = new TreeSet<>(data);
    }

    public static Set<JkEntity> createProxySet(RepoDataHandler repoDataHandler) {
        return createProxySet(repoDataHandler, Collections.emptyList());
    }
    public static Set<JkEntity> createProxySet(RepoDataHandler repoDataHandler, Collection<JkEntity> data) {
        HandlerDataSet handler = new HandlerDataSet(repoDataHandler, data);
        ClassLoader loader = TreeSet.class.getClassLoader();
        Class[] interfaces = {Set.class};
        return (Set<JkEntity>) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // todo impl
        synchronized (dataSet) {
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
