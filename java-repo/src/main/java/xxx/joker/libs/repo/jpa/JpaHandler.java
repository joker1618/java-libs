package xxx.joker.libs.repo.jpa;

import xxx.joker.libs.repo.config.RepoCtx;
import xxx.joker.libs.repo.design.RepoEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public interface JpaHandler {

    static JpaHandler createHandler(RepoCtx ctx) {
        return new JpaHandlerImpl(ctx);
    }

    <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters);
    Map<Class<? extends RepoEntity>, Set<RepoEntity>> getDataSets();
    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);
    Map<Long, RepoEntity> getDataById();

    void initRepoContent(Collection<RepoEntity> repoData);

    void clearAll(boolean resetIdSequence);

    boolean commit();
    boolean rollback();

    String getProperty(String key);
    String setProperty(String key, String value);
    String delProperty(String key);

    void updateDependencies(Collection<? extends RepoEntity> entities);
    void removeFromDependencies(RepoEntity toRemove, Collection<? extends RepoEntity> entities);

}
