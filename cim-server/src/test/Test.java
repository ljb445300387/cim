/**
 * @author laijianbo
 * @date 2019/10/18 14:01
 */
public class Test {

    public static void main(String[] args) {
        FizzBuzz fizzBuzz = new FizzBuzz(16);
        new Thread(() -> {
            try {
                fizzBuzz.fizz(() -> {
                    System.out.println("fizz");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> {
                    System.out.println("buzz");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> {
                    System.out.println("fizzbuzz");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                fizzBuzz.number((value) -> {
                    System.out.println(value);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
