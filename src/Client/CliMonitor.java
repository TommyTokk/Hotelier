package Client;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class CliMonitor {
    private Lock lock = new ReentrantLock();

    public Lock lock() {
        lock.lock();
        return lock;
    }

    public void unlock() {
        lock.unlock();
    }


}
