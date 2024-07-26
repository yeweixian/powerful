package com.dangerye.powerful.trial;

public class TestMain {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                final TestA instance = Singleton.getInstance(TestA.class);
                System.out.println(instance);
            }).start();
            new Thread(() -> {
                final TestB instance = Singleton.getInstance(TestB.class);
                System.out.println(instance);
            }).start();
        }
        System.in.read();
    }

    final static class TestA {
        private TestA() {
        }

        private static TestA getInstance() {
            return new TestA();
        }
    }

    final static class TestB {
        private TestB() {
        }

        private static TestB getInstance() {
            return new TestB();
        }
    }
}
