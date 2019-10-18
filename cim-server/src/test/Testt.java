import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

class FizzBuzz {
    private int n;
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore semaphore35 = new Semaphore(0);
    private Semaphore semaphore3 = new Semaphore(0);
    private Semaphore semaphore5 = new Semaphore(0);
    private int i;

    public FizzBuzz(int n) {
        this.n = n;
    }

    // printFizz.regist() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            semaphore3.acquire();
            if (i > n) {
                break;
            }
            printFizz.run();
            semaphore.release();
        }
    }

    // printBuzz.regist() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            semaphore5.acquire();
            if (i > n) {
                break;
            }
            printBuzz.run();
            semaphore.release();
        }
    }

    // printFizzBuzz.regist() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            semaphore35.acquire();
            if (i > n) {
                break;
            }
            printFizzBuzz.run();
            semaphore.release();
        }

    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        for (i = 1; i <= n; i++) {
            if (checkThreeAndFive(i)) {
                semaphore35.release();
                semaphore.acquire();
                continue;
            }

            if (checkThree(i)) {
                semaphore3.release();
                semaphore.acquire();
                continue;
            }

            if (checkFive(i)) {
                semaphore5.release();
                semaphore.acquire();
                continue;
            }
            printNumber.accept(i);
        }
        semaphore3.release();
        semaphore35.release();
        semaphore5.release();

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