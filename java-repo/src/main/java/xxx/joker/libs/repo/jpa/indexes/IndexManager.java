package xxx.joker.libs.repo.jpa.indexes;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IndexManager {

    private ReentrantReadWriteLock lock;
    private final AtomicLong mainSequence;

    public IndexManager(long mainSequence) {
        this.mainSequence = new AtomicLong(mainSequence);
        this.lock = new ReentrantReadWriteLock(true);
    }

    public synchronized void setSequenceValue(long value) {
        mainSequence.set(value);;
    }

    public synchronized long getSequenceValueAndLock() {
        lock.writeLock().lock();
        return getSequenceValue();
    }

    public synchronized void unlockSequence(boolean increment) {
        if(lock.isWriteLocked()) {
            if(increment)   mainSequence.getAndIncrement();
            lock.writeLock().unlock();
        }
    }

    public synchronized long getSequenceValue() {
        return mainSequence.get();
    }

}
