package xxx.joker.libs.repository.managers;

import xxx.joker.libs.repository.design.JkEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;

public class HandlerSet implements InvocationHandler {

    private final RepoDataHandler repoHandler;
    private final TreeSet<Comparable> sourceSet;

    private HandlerSet(RepoDataHandler repoHandler, Collection<? extends Comparable> data) {
        this.repoHandler = repoHandler;
        this.sourceSet = new TreeSet<>(data);
    }

    public static Set<Comparable> createProxySet(RepoDataHandler repoDataHandler) {
        return createProxySet(repoDataHandler, Collections.emptyList());
    }

    public static Set<Comparable> createProxySet(RepoDataHandler repoDataHandler, Collection<? extends Comparable> data) {
        HandlerSet handler = new HandlerSet(repoDataHandler, data);
        ClassLoader loader = TreeSet.class.getClassLoader();
        Class[] interfaces = {Set.class};
        return (Set<Comparable>) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if ("add".equals(method.getName())) {
            JkEntity e = (JkEntity) args[0];
            return addEntity(e);

        } else if ("addAll".equals(method.getName())) {
            Collection coll = (Collection) args[0];
            boolean res = false;
            for (Object obj : coll) {
                JkEntity e = (JkEntity) obj;
                res &= addEntity(e);
            }
            return res;
        }

        return method.invoke(sourceSet, args);
    }

    private boolean addEntity(JkEntity e) {
        Lock writeLock = repoHandler.getWriteLock();
        try {
            writeLock.lock();
            if (e.getEntityID() == null) {
                e.setEntityID(repoHandler.getSequenceValue());
                e.setInsertTstamp(LocalDateTime.now());
            }

            boolean added = sourceSet.add(e);
            if (added) {
                repoHandler.incrementSequence();
            }

            return added;

        } finally {
            writeLock.unlock();
        }
    }

}
