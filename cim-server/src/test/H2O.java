import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class H2O {

    CyclicBarrier barrier = new CyclicBarrier(3);
    CyclicBarrier barrierH = new CyclicBarrier(2);
    Semaphore semaphoreh = new Semaphore(2);
    Semaphore semaphore0 = new Semaphore(1);

    private int hcount = 2;
    private int ocount = 1;
    private ReentrantLock lock = new ReentrantLock();

    public H2O() {

    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        semaphoreh.acquire();
        releaseHydrogen.run();
        if (semaphoreh.availablePermits() == 0) {
            semaphore0.release();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        semaphore0.acquire();
        releaseOxygen.run();
        if (semaphore0.availablePermits() < 1) {
            semaphore0.release();
        }
    }
}