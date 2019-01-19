package xxx.joker.libs.repository.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.design.JkEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class HandlerSet implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(HandlerSet.class);

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

        String methodName = method.getName();

        if ("add".equals(methodName)) {
            logger.trace("invoked {}", methodName);
            JkEntity e = (JkEntity) args[0];
            repoHandler.addEntity(e);

        } else if ("addAll".equals(methodName)) {
            logger.trace("invoked {}", methodName);
            Collection coll = (Collection) args[0];
            List<JkEntity> elist = JkStreams.map(coll, ce -> (JkEntity)ce);
            elist.forEach(repoHandler::addEntity);
        }

        return method.invoke(sourceSet, args);
    }



}
