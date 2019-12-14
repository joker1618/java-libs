package xxx.joker.libs.repo.jpa.proxy;

import xxx.joker.libs.core.runtime.wrapper.TypeWrapper;
import xxx.joker.libs.repo.design.RepoEntity;
import xxx.joker.libs.repo.wrapper.RepoWField;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyMap implements InvocationHandler {

    private final Map sourceMap;
    private final ReadWriteLock lock;
    private final Function<RepoEntity, Boolean> addFunction;
    private final RepoWField fw;
    private final ProxyFactory proxyFactory;


    public ProxyMap(Map sourceMap, ReadWriteLock lock, Function<RepoEntity, Boolean> addFunction, RepoWField fw, ProxyFactory proxyFactory) {
        this.sourceMap = sourceMap;
        this.lock = lock;
        this.addFunction = addFunction;
        this.fw = fw;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Lock actualLock = ProxyFactory.WRITE_METHODS_MAP.contains(methodName) ? lock.writeLock() : lock.readLock();

        try {
            actualLock.lock();

            if ("put".equals(methodName)) {
                return doPut(args);
            }

            if ("putAll".equals(methodName)) {
                Map<?,?> inputMap = (Map) args[0];
                inputMap.forEach((k,v) -> doPut(k, v));
                return null;
            }

            if ("putIfAbsent".equals(methodName)) {
                Object ret = null;
                if(!sourceMap.containsKey(args[0])) {
                    ret = doPut(args);
                }
                return ret;
            }

            if ("replace".equals(methodName)) {
                Object ret = null;
                if(args.length == 2 && sourceMap.containsKey(args[0])) {
                    ret = doPut(args);
                } else if(args.length == 3) {
                    if(sourceMap.containsKey(args[0]) && Objects.equals(sourceMap.get(args[0]), args[1])) {
                        doPut(args[0], args[2]);
                        ret = true;
                    } else {
                        ret = false;
                    }
                }
                return ret;
            }

            if ("replaceAll".equals(methodName)) {
                BiFunction function = (BiFunction) args[0];
                for (Object key : sourceMap.keySet()) {
                    Object value = sourceMap.get(key);
                    doPut(key, function.apply(key, value));
                }
                return null;
            }

            if ("compute".equals(methodName)) {
                Object key = args[0];
                BiFunction remappingFunction = (BiFunction) args[1];
                Object oldValue = sourceMap.get(key);
                Object newValue = remappingFunction.apply(key, oldValue);
                if (oldValue != null) {
                    if (newValue != null) {
                        doPut(key, newValue);
                    } else {
                        sourceMap.remove(key);
                    }
                } else {
                    if (newValue != null) {
                        doPut(key, newValue);
                    } else {
                        return null;
                    }
                }
                return sourceMap.get(key);
            }

            if ("computeIfPresent".equals(methodName)) {
                Object key = args[0];
                BiFunction remappingFunction = (BiFunction) args[1];
                Object oldValue = sourceMap.get(key);
                if (oldValue != null) {
                    Object newValue = remappingFunction.apply(key, oldValue);
                    if (newValue != null) {
                        doPut(key, newValue);
                    } else {
                        sourceMap.remove(key);
                    }
                }
                return sourceMap.get(key);
            }

            if ("computeIfAbsent".equals(methodName)) {
                Object key = args[0];
                Function mappingFunction = (Function) args[1];
                Object value = sourceMap.get(key);
                if (value == null) {
                    Object newValue = mappingFunction.apply(key);
                    if (newValue != null) {
                        doPut(key, newValue);
                    }
                }
                return value;
            }

            if ("merge".equals(methodName)) {
                Object key = args[0];
                Object value = args[1];
                BiFunction remappingFunction = (BiFunction) args[2];
                Object oldValue = sourceMap.get(key);
                Object newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
                if (newValue == null) {
                    sourceMap.remove(key);
                } else {
                    doPut(key, newValue);
                }
                return sourceMap.get(key);
            }

            return method.invoke(sourceMap, args);

        } finally {
            actualLock.unlock();
        }
    }

    private Object doPut(Object... args) {
        Object key = args[0];
        if(fw.getParamType(0).instanceOf(RepoEntity.class)) {
            RepoEntity reKey = (RepoEntity) key;
            addFunction.apply(reKey);
        }
        Object value = args[1];
        Object finalValue;
        TypeWrapper fwValue = fw.getParamType(1);
        if(fwValue.instanceOf(RepoEntity.class)) {
            RepoEntity reValue = (RepoEntity) value;
            addFunction.apply(reValue);
            finalValue = reValue;
        } else if(fwValue.isCollection() && fwValue.getParamType(0).instanceOf(RepoEntity.class)) {
            Collection<RepoEntity> coll = (Collection<RepoEntity>) value;
            coll.forEach(addFunction::apply);
            finalValue = fwValue.isList() ? proxyFactory.createProxyList(coll) : proxyFactory.createProxySet(coll);
        } else {
            finalValue = value;
        }
        return sourceMap.put(key, finalValue);
    }

    public Map<?,?> getSourceMap() {
        return sourceMap;
    }
}