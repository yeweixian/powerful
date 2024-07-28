package com.dangerye.powerful.trial;

public class TestMain {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                final Loader<TestA> loader = SingletonLoader.getLoader(TestA.class);
                final TestA obj;
                final TestA tryGet = loader.get();
                if (tryGet == null) {
                    synchronized (loader) {
                        final TestA testA = loader.get();
                        if (testA == null) {
                            final TestA test = new TestA(SingletonLoader.getLoader(TestB.class));
                            loader.set(test);
                            obj = test;
                        } else {
                            obj = testA;
                        }
                    }
                } else {
                    obj = tryGet;
                }
                System.out.println(obj);
                obj.printTestB();
            }).start();
            new Thread(() -> {
                final Loader<TestB> loader = SingletonLoader.getLoader(TestB.class);
                final TestB obj;
                final TestB tryGet = loader.get();
                if (tryGet == null) {
                    synchronized (loader) {
                        final TestB testB = loader.get();
                        if (testB == null) {
                            final TestB test = new TestB(SingletonLoader.getLoader(TestA.class));
                            loader.set(test);
                            obj = test;
                        } else {
                            obj = testB;
                        }
                    }
                } else {
                    obj = tryGet;
                }
                System.out.println(obj);
                obj.printTestA();
            }).start();
            new Thread(() -> {
                final TestA instance = SingletonLoader.getInstance(TestA.class);
                System.out.println(instance);
                instance.printTestB();
            }).start();
            new Thread(() -> {
                final TestB instance = SingletonLoader.getInstance(TestB.class);
                System.out.println(instance);
                instance.printTestA();
            }).start();
        }
        System.in.read();
    }

    final static class TestA {
        private final Loader<TestB> testBLoader;

        private TestA(Loader<TestB> testBLoader) {
            this.testBLoader = testBLoader;
        }

        private static TestA getInstance() {
            return new TestA(SingletonLoader.getLoader(TestB.class));
        }

        public void printTestB() {
            final TestB instance = testBLoader.getInstance();
            System.out.println(instance);
        }
    }

    final static class TestB {
        private final Loader<TestA> testALoader;

        private TestB(Loader<TestA> testALoader) {
            this.testALoader = testALoader;
        }

        private static TestB getInstance() {
            return new TestB(SingletonLoader.getLoader(TestA.class));
        }

        public void printTestA() {
            final TestA instance = testALoader.getInstance();
            System.out.println(instance);
        }
    }
}
