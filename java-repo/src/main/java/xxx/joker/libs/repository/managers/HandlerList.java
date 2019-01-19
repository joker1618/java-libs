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
import java.util.*;

public class HandlerList implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(HandlerList.class);

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
    public Object invoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String methodName = method.getName();

        if (StringUtils.equalsAny(methodName, "add", "set")) {
            logger.trace("invoked {}", methodName);
            int argIndex = "set".equals(method.getName()) ? 1 : 0;
            JkEntity e = (JkEntity) args[argIndex];
            repoHandler.addEntity(e);

        } else if ("addAll".equals(method.getName())) {
            logger.trace("invoked {} ({})", methodName, Arrays.toString(args));
            Collection coll = (Collection) args[args.length-1];
            List<JkEntity> elist = JkStreams.map(coll, ce -> (JkEntity)ce);
            elist.forEach(repoHandler::addEntity);
        }

        return method.invoke(sourceList, args);
    }
}
