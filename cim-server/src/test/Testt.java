import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class FizzBuzz {
    private int n;
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore semaphore35 = new Semaphore(0);
    private Semaphore semaphore3 = new Semaphore(0);
    private Semaphore semaphore5 = new Semaphore(0);

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition3 = lock.newCondition();
    private Condition condition5 = lock.newCondition();
    private Condition condition35 = lock.newCondition();
    private Condition condition = lock.newCondition();

    private boolean flag3 = false;
    private boolean flag5 = false;
    private boolean flag35 = false;
    private boolean flag = true;

    public FizzBuzz(int n) {
        this.n = n;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 3; i <= n; i += 3) {
                if (checkThreeAndFive(i)) {
                    continue;
                }
                while (!flag3) {
                    condition3.await();
                }
                printFizz.run();
                flag = true;
                flag3 = false;
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 5; i <= n; i += 5) {
                if (checkThreeAndFive(i)) {
                    continue;
                }
                while (!flag5) {
                    condition5.await();
                }
                printBuzz.run();
                flag = true;
                flag5 = false;
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 15; i <= n; i += 15) {
                while (!flag35) {
                    condition35.await();
                }
                printFizzBuzz.run();
                flag = true;
                flag35 = false;
                condition.signal();
            }
        } finally {
            lock.unlock();
        }

    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 1; i <= n; i++) {
                if (checkThreeAndFive(i)) {
                    flag35 = true;
                    condition35.signal();
                    flag = false;
                    while (!flag) {
                        condition.await();
                    }
                    continue;
                }

                if (checkThree(i)) {
                    flag3 = true;
                    condition3.signal();
                    flag = false;
                    while (!flag) {
                        condition.await();
                    }
                    continue;
                }

                if (checkFive(i)) {
                    flag5 = true;
                    condition5.signal();
                    flag = false;
                    while (!flag) {
                        condition.await();
                    }
                    continue;
                }
                printNumber.accept(i);
            }
        } finally {
            lock.unlock();
        }

    }

    boolean checkFive(int i) {
        return i % 5 == 0;
    }


    boolean checkThree(int i) {
        return i % 3 == 0;
    }

    boolean checkThreeAndFive(int i) {
        return i % 3 == 0 && i % 5 == 0;
    }

}