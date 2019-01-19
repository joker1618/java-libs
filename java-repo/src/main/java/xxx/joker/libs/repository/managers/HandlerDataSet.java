package xxx.joker.libs.repository.managers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.design.JkEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class HandlerDataSet implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(HandlerDataSet.class);

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
    public Object invoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String methodName = method.getName();

        if (StringUtils.equalsAny(methodName, "addAll", "add")) {
            logger.trace("invoked {}", methodName);
            Collection coll;
            if("add".equals(method.getName())) {
                coll = Collections.singletonList(args[0]);
            } else {
                coll = (Collection) args[0];
            }
            List toAdd = new ArrayList();
            coll.forEach(c -> { if(canBeAdd(c)) toAdd.add(c); });
            return toAdd.isEmpty() ? false : method.invoke(dataSet, toAdd);
        }

        if (StringUtils.equalsAny(methodName, "removeAll", "removeIf", "remove", "clear")) {
            logger.trace("invoked {}", methodName);
            Collection coll;
            if("remove".equals(method.getName())) {
                coll = Collections.singletonList(args[0]);
            } else if("removeAll".equals(method.getName())) {
                coll = (Collection) args[0];
            } else if("clear".equals(method.getName())) {
                coll = dataSet;
            } else {
                Predicate<JkEntity> filter = (Predicate<JkEntity>) args[0];
                coll = JkStreams.filter(dataSet, filter);
            }
            List toRemove = new ArrayList();
            coll.forEach(c -> { if(canBeRemoved(c)) toRemove.add(c); });
            return coll.isEmpty() ? false : method.invoke(dataSet, toRemove);
        }

        return method.invoke(dataSet, args);
    }

    private boolean canBeAdd(Object o) {
        JkEntity e = (JkEntity) o;
        boolean exists = repoHandler.setIdAndCheckIfExists(e);
        if(!exists){
            repoHandler.addDependencies(e);
            return true;
        }
        return false;
    }
    private boolean canBeRemoved(Object o) {
        JkEntity e = (JkEntity) o;
        if(!e.isRegistered() || !dataSet.contains(e))   return false;
        repoHandler.removeFromDependencies(e);
        return true;
    }
}
