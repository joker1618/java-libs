package xxx.joker.libs.repo.jpa.proxy;

import xxx.joker.libs.repo.design.RepoEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import static xxx.joker.libs.core.lambda.JkStreams.filter;

public class ProxySet implements InvocationHandler {

    private final ReadWriteLock lock;
    private final Function<RepoEntity, Boolean> addFunction;
    private final TreeSet<RepoEntity> sourceSet;

    public ProxySet(ReadWriteLock lock, Collection<RepoEntity> sourceData, Function<RepoEntity, Boolean> addFunction) {
        this.lock = lock;
        this.addFunction = addFunction;
        this.sourceSet = new TreeSet<>(sourceData);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Lock actualLock = ProxyFactory.WRITE_METHODS_COLLECTION.contains(methodName) ? lock.writeLock() : lock.readLock();

        try {
            actualLock.lock();

            if ("add".equals(methodName)) {
                RepoEntity e = (RepoEntity) args[0];
                addFunction.apply(e);
                return sourceSet.add(e);
            }

            if ("addAll".equals(methodName)) {
                Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                coll.forEach(addFunction::apply);
                return sourceSet.addAll(coll);
            }

            return method.invoke(sourceSet, args);

        } finally {
            actualLock.unlock();
        }
    }

    public Set<RepoEntity> getSourceSet() {
        return sourceSet;
    }
}