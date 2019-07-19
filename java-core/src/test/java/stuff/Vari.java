package stuff;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Vari {

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