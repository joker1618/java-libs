package xxx.joker.libs.repo.jpa.proxy;

import xxx.joker.libs.repo.design.RepoEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import static xxx.joker.libs.core.lambda.JkStreams.filter;

public class ProxyList implements InvocationHandler {

    private final List<RepoEntity> sourceList;
    private final ReadWriteLock lock;
    private final Function<RepoEntity, Boolean> addFunction;
    private final Consumer<Long> addIndexConsumer;

    public ProxyList(Collection<RepoEntity> sourceList, ReadWriteLock lock, Function<RepoEntity, Boolean> addFunction, Consumer<Long> addIndexConsumer) {
        this.sourceList = new ArrayList<>(sourceList);
        this.lock = lock;
        this.addFunction = addFunction;
        this.addIndexConsumer = addIndexConsumer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Lock actualLock = ProxyFactory.WRITE_METHODS_COLLECTION.contains(methodName) ? lock.writeLock() : lock.readLock();

        try {
            actualLock.lock();

            if ("add".equals(methodName)) {
                if(args.length == 1) {
                    RepoEntity e = (RepoEntity) args[0];
                    addFunction.apply(e);
                    boolean res = sourceList.add(e);
                    addIndexConsumer.accept(e.getEntityId());
                    return res;
                } else {
                    int pos = (int) args[0];
                    RepoEntity e = (RepoEntity) args[1];
                    addFunction.apply(e);
                    sourceList.add(pos, e);
                    addIndexConsumer.accept(e.getEntityId());
                    return null;
                }
            }

            if ("addAll".equals(methodName)) {
                if(args.length == 1) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    coll.forEach(addFunction::apply);
                    boolean res = sourceList.addAll(coll);
                    filter(coll, sourceList::contains).forEach(re -> addIndexConsumer.accept(re.getEntityId()));
                    return res;
                } else {
                    int pos = (int) args[0];
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[1];
                    coll.forEach(addFunction::apply);
                    boolean res = sourceList.addAll(pos, coll);
                    filter(coll, sourceList::contains).forEach(re -> addIndexConsumer.accept(re.getEntityId()));
                    return res;
                }
            }

            if ("set".equals(methodName)) {
                int pos = (int) args[0];
                RepoEntity e = (RepoEntity) args[1];
                addFunction.apply(e);
                Object res = sourceList.set(pos, e);
                addIndexConsumer.accept(e.getEntityId());
                return res;
            }

            return method.invoke(sourceList, args);

        } finally {
            actualLock.unlock();
        }
    }

    public List<RepoEntity> getSourceList() {
        return sourceList;
    }
}