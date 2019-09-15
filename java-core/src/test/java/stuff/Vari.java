package stuff;

import org.junit.Test;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Vari {

    private String str;
    private final String strFinal = "ll";

    @Test
    public void inadfieee() {
        List<Field> allFields = JkReflection.findAllFields(getClass());
        allFields.forEach(f -> display(f.toGenericString()));
    }

    @Test
    public void inieee() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        lock.writeLock().lock();
        lock.readLock().lock();
        lock.writeLock().lock();
        lock.readLock().unlock();
        lock.writeLock().unlock();
        lock.writeLock().unlock();

        lock.writeLock().lock();
        lock.readLock().lock();
        lock.writeLock().unlock();
        lock.writeLock().lock();
        lock.readLock().unlock();
        lock.writeLock().unlock();
    }
}