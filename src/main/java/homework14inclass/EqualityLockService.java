package homework14inclass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EqualityLockService implements Service {
    private final Service service;
    private Map<Object, ReentrantLock> map = new ConcurrentHashMap<>();
    private final Object mapGuardian = new Object();

    public EqualityLockService(Service service) {
        this.service = service;
    }

    @Override
    public void run(Object o) {
        ReentrantLock lock = getLock(o);
        lock.lock();
        try {
            service.run(o);
        } finally {
            lock.unlock();
            synchronized (mapGuardian) {
                if (lock.getQueueLength() == 0) {
                    map.remove(o);
                }
            }
        }
    }

    private ReentrantLock getLock(Object o) {
        synchronized (mapGuardian) {
            return map.putIfAbsent(o, new ReentrantLock());
        }
    }
}
