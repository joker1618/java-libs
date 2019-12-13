package xxx.joker.libs.repo.jpa.proxy;

import xxx.joker.libs.repo.design.RepoEntity;
import xxx.joker.libs.repo.jpa.indexes.IndexManager;
import xxx.joker.libs.repo.wrapper.RepoWField;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyFactory {

    static final List<String> WRITE_METHODS_COLLECTION = Arrays.asList(
            "add", "addAll", "remove", "removeIf", "removeAll", "clear", "set"
    );
    static final List<String> WRITE_METHODS_MAP = Arrays.asList(
            "put", "putAll", "putIfAbsent", "replace", "replaceAll", "compute", "computeIfPresent", "computeIfAbsent", "merge", "remove", "clear"
    );

    private final ReadWriteLock lock;
    private final Function<RepoEntity, Boolean> addFunction;
    private final Function<RepoEntity, Boolean> removeFunction;
    private final IndexManager indexManager;

    public ProxyFactory(ReadWriteLock lock, Function<RepoEntity, Boolean> addFunction, Function<RepoEntity, Boolean> removeFunction, IndexManager indexManager) {
        this.lock = lock;
        this.addFunction = addFunction;
        this.removeFunction = removeFunction;
        this.indexManager = indexManager;
    }

    public ProxyDataSet createProxyDataSet() {
        return createProxyDataSet(Collections.emptyList());
    }
    public ProxyDataSet createProxyDataSet(Collection<RepoEntity> data) {
        Collection<RepoEntity> finalData = data == null ? Collections.emptyList() : data;
        return new ProxyDataSet(lock, finalData, addFunction, removeFunction);
    }

    public Set<RepoEntity> createProxySet(Collection<RepoEntity> data, RepoEntity parent, RepoWField wField) {
        return createProxySet(data, createAddIndexConsumer(parent, wField));
    }
    public Set<RepoEntity> createProxySet(Collection<RepoEntity> data, Consumer<Long> addIndexConsumer) {
        ClassLoader loader = TreeSet.class.getClassLoader();
        Class[] interfaces = {Set.class};
        Collection<RepoEntity> finalData = data == null ? Collections.emptyList() : data;
        ProxySet proxySet = new ProxySet(lock, finalData, addFunction, addIndexConsumer);
        return (Set<RepoEntity>) Proxy.newProxyInstance(loader, interfaces, proxySet);
    }

    public List<RepoEntity> createProxyList(Collection<RepoEntity> data, RepoEntity parent, RepoWField wField) {
        return createProxyList(data, createAddIndexConsumer(parent, wField));
    }
    public List<RepoEntity> createProxyList(Collection<RepoEntity> data, Consumer<Long> addIndexConsumer) {
        ClassLoader loader = ArrayList.class.getClassLoader();
        Class[] interfaces = {List.class};
        Collection<RepoEntity> finalData = data == null ? Collections.emptyList() : data;
        ProxyList proxyList = new ProxyList(finalData, lock, addFunction, addIndexConsumer);
        return (List<RepoEntity>) Proxy.newProxyInstance(loader, interfaces, proxyList);
    }

    public Map<?, ?> createProxyMap(Map<?,?> map, RepoEntity parent, RepoWField wField) {
        Class<?> mapClass = map instanceof TreeMap ? TreeMap.class : LinkedHashMap.class;
        ClassLoader loader = mapClass.getClassLoader();
        Class[] interfaces = {Map.class};
        Map<?,?> finalData = map == null ? new LinkedHashMap<>() : map;
        Consumer<Long> addIndexConsumer = createAddIndexConsumer(parent, wField);
        ProxyMap proxyMap = new ProxyMap(finalData, lock, addFunction, wField, this, addIndexConsumer);
        return (Map<?,?>) Proxy.newProxyInstance(loader, interfaces, proxyMap);
    }

    private Consumer<Long> createAddIndexConsumer(RepoEntity parent, RepoWField wField) {
        return id -> indexManager.addUsage(id, parent.getEntityId(), wField);
    }
}
