package Multithreading;

class MyThread extends Thread {
    public void run() {
        System.out.println(Thread.currentThread());
        try {
            // making the thread sleep for 2 seconds
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("This is the running thread!");
    }
}
public class ThreadCreation {

    public static Object sharedObject = new Object();
    public static void main(String[] args) {

        // basic and inefficient approaches to create threads
//        createThreadUsingCustomThreadClass();
//        createThreadAndInitUsingLambdaExp();

        // Difference between using Thread class vs Runnable interface
        /* The most common difference is:
         - When you extend Thread class, you can’t extend any other class which you require.
         - As you know, Java does not allow inheriting more than one class.
         - When you implement Runnable, you can save a space for your class to extend any other class in future or now.
        However, the significant difference is:
         - When you extend Thread class, each of your thread creates unique object and associate with it.
         - When you implement Runnable, it shares the same object to multiple threads.
         */
//        createThreadUsingRunnableInterface();

        // Daemon threads
//        daemonThreadExample();

        // Java memory model in multi threading program
        javaMemoryModelInMultiThreading();


    }

    private static void javaMemoryModelInMultiThreading() {
        /*
        In Java, JVM consist of [heap memory + Thread stack memory for each thread]
        - Thread memory stores local variable for a particular thread.
        - Shared objects are stored in the Heap memory. Thread stack only contains the reference to those shared object.
        - Hence we need to be careful here, as multiple threads accessing same object
         */
        Runnable runnable1 = () -> {
            System.out.println("Thread 1 started => name: " + Thread.currentThread().getName() + ", state: " + Thread.currentThread().getState());
            System.out.println(sharedObject.hashCode());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 1 completed!");
        };

        Runnable runnable2 = () -> {
            System.out.println("Thread 2 started => name: " + Thread.currentThread().getName() + ", state: " + Thread.currentThread().getState());
            System.out.println(sharedObject.hashCode());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 2 completed!");
        };
        Thread th1 = new Thread(runnable1);
        th1.start();
        Thread th2 = new Thread(runnable2);
        th2.start();
    }

    private static void daemonThreadExample() {
        Runnable runnable = () -> {
            System.out.println("Thread started! - " + Thread.currentThread().getName());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread ended! - " + Thread.currentThread().getName());
        };
        Thread thread = new Thread(runnable, "th-1");
        // Daemon thread - The JVM exits when the only threads running are all daemon threads.
        // Main thread will not wait for Daemon thread and will exit the process
        // This method must be invoked before the thread is started.
        thread.setDaemon(true);
        thread.start();
        System.out.println("Main thread end here! - " + Thread.currentThread().getName());
    }

    private static void createThreadUsingRunnableInterface() {
        Runnable runnable = new Runnable() {
            /*
             Runnable is a FUNCTIONAL INTERFACE and run is an abstract method which must be implemented in the non-abstract child class.
             Any interface with a SAM(Single Abstract Method) is a functional interface.
             */
            @Override
            public void run() {
                System.out.println("This is a runnable thread!");
            }
        };
        Thread thread1 = new Thread(runnable, "runnable-thread");
        thread1.start();

        Runnable runnableUsingLambda = () -> {
            System.out.println("This is a runnable thread using lambda!");
        };

        Thread thread2 = new Thread(runnableUsingLambda, "runnable-lambda-thread");
        thread2.start();
    }

    private static void createThreadAndInitUsingLambdaExp() {
        Thread threadObj = new Thread(() -> {
            System.out.println("This thread is running");
        });
        threadObj.start();
    }

    private static void createThreadUsingCustomThreadClass() {
        System.out.println(Thread.currentThread());
        MyThread myThread = new MyThread();
        /*
         NOTE:
         1. If I use the run() method, it will be executed in the same thread i.e. main thread.
         2. If I use the start() method, it will be executed in the different thread.
         start() method first start the execution of thread then the JVM executes the run() method of that thread
         */
        System.out.println("**** Testing run() method ****");
        long s1 = System.currentTimeMillis();
        myThread.run();
        myThread.run();
        System.out.println("Time taken in execution of run methods: " + (System.currentTimeMillis() - s1));
        System.out.println("**** Now testing start() method ****");
        long s2 = System.currentTimeMillis();
        myThread.start();
        /*
        2nd start call will cause exception because:
        - A thread object is associated with its state - NEW, RUNNABLE, RUNNING, WAITING, TERMINATED.
        - When you create an instance of a thread using new keyword, it will be in NEW state.
        - When start() method is invoked on the thread, its state will be transitioned from NEW to RUNNABLE and
          it will be picked up by thread scheduler for processing. You can invoke start() method on a thread only
          if its state is NEW.
        - When start() method is called twice, the second call will throw IllegalThreadStateException as the thread
          state is already changed to RUNNABLE by first call.
        -IllegalThreadStateException is Runtime exception (unchecked exception).
         */
        try{
            myThread.start();
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getClass());
        }
        System.out.println("Time taken in execution of start methods: " + (System.currentTimeMillis() - s2));
    }
}
