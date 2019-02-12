import java.lang.*;
import java.util.concurrent.*; 
import java.util.Scanner;


public class DiningPhilosphers {
    public static void main(String[] args){
        int num = 5;
        Scanner sc = new Scanner(System.in);

        System.out.println("How many philosphers?");
        if(sc.hasNextInt()){
            num = sc.nextInt();
        } 

        if (num == 1) {
            System.out.println("Philospher 1 wants to eat but can't since there is only 1 fork available?");
            System.exit(1);
        }
        Semaphore[] forks = new Semaphore[num];
        PhilospherThread[] philosphers = new PhilospherThread[num];

        // Create philosphers and forks, assuming they're in a circle
        for(int i = 0; i < num; i++){
            Semaphore fork = new Semaphore(1);
            PhilospherThread philospher;
            // create first fork
            if(i == 0)
                forks[i] = fork;

            // assign fork for the last philospher
            if(i == (num-1)){
                philospher = new PhilospherThread("Philospher "+ (i+1), forks[i], forks[0]);
            }
            else {
                Semaphore nextFork = new Semaphore(1);
                forks[i+1] = nextFork;
                philospher = new PhilospherThread("Philospher "+ (i+1), forks[i], forks[i+1]);
            }
            philosphers[i] = philospher;
        }
        startThreads(philosphers);
        checkIfEveryoneHasEaten(philosphers);

        
    }

    public static void startThreads(PhilospherThread[] philosphers){
        for(int j = 0; j < philosphers.length; j++)
                philosphers[j].start();
        
    }
    
    public static void checkIfEveryoneHasEaten(PhilospherThread[] philosphers){
        try {
                for(int k = 0; k < philosphers.length; k++){
                    philosphers[k].join();
                }
            } catch (InterruptedException e){

            }
            System.out.println("*** EVERYONE HAS EATEN ***");
    }
}



class PhilospherThread extends Thread {
    String name;
    Semaphore leftFork;
    Semaphore rightFork;
    public PhilospherThread(String name, Semaphore leftFork, Semaphore rightFork){
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    public void run(){
        System.out.println(name + " wants to eat...");
        try {
            leftFork.acquire();
            rightFork.acquire();

            System.out.println(name + " has acquired both forks and is eating.");
            sleep(2000);
            System.out.println(name + " HAS EATEN!!");
        } catch(InterruptedException e){
            System.out.println(e);
        }
        
        leftFork.release();
        rightFork.release();
        System.out.println(name + " has released both forks.");

    }
}

