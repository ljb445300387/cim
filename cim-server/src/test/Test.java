/**
 * @author laijianbo
 * @date 2019/10/18 14:01
 */
public class Test {

    public static void main(String[] args) {
        H2O fizzBuzz = new H2O();
        new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    fizzBuzz.hydrogen(() -> {
                        System.out.println("H");
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    fizzBuzz.oxygen(() -> {
                        System.out.println("O");
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    fizzBuzz.oxygen(() -> {
                        System.out.println("O");
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
