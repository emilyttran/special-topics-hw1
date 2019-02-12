import java.util.Random;
import java.lang.*;
import java.util.concurrent.*;
import java.text.*;
 

public class Roulette {
    static Results result = new Results();
    static int numThreads;
    static int NUM_CALCULATIONS = 10000000;
    static long t1 = 0;
    static long t2 = 0;

    public static void main(String[] args){

        if(args.length != 1){
            System.err.println("Usage: java Roulette <number of threads>");
            System.exit(1);
        }
        numThreads = Integer.parseInt(args[0]);
        t1 = System.nanoTime();
        Thread[] threads = runThreads();
        double RTPFinal = returnRTPFinal(threads);
        long totalTime = (t2 - t1) /1000000000;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println();
        System.out.println("***ALL " +  numThreads + " THREADS HAVE FINISHED CALCULATING " + NUM_CALCULATIONS + " SIMULATIONS.***" );
        System.out.println("SIMULATION TIME -----> " + totalTime + " seconds.");
        System.out.println("FINAL RTP       -----> " + df.format(RTPFinal * 100) + "%");
    }  


    public static double returnRTPFinal(Thread[] threads){
        try{
            for(Thread thread : threads)
                thread.join();

        } catch (InterruptedException ie){
            System.out.println(ie);
            }
        t2 = System.nanoTime();
        return result.RTPTotal / numThreads;

    }

    public static Thread[] runThreads(){
        Thread[] threads = new RTPThread[numThreads];

        // Figure out how many calculations per thread
        int calcLeft = NUM_CALCULATIONS;
        int numCalculations = calcLeft/numThreads;
        int remainders = calcLeft % numThreads;
        Semaphore permissionToAdd = new Semaphore(1);

    // Create threads based on numThreads, including the remainder
    for(int i = 0; i < numThreads; i++){
        RTPThread thread;
        if(calcLeft > numCalculations + remainders){
            thread = new RTPThread(i, numCalculations, permissionToAdd, result); 
            calcLeft = calcLeft - numCalculations;
        } else
            thread = new RTPThread(i, calcLeft, permissionToAdd, result); 
       threads[i] = thread;
       thread.start();
        }

        return threads;
    }
}

class RTPThread extends Thread {
    int threadNum;
    int numExecution;
    double total;
    Semaphore sem;
    Results result;

    public RTPThread(int threadNum, int numCalculations, Semaphore sem, Results result){
        this.threadNum = threadNum;
        this.numExecution = numCalculations;
        this.sem = sem;
        this.result = result;
    }

    public void run(){
        System.out.println("Thread " + threadNum + " started. Calculating " + numExecution + " times.");
        double winning;
        double totalWinning = 0;
        double bet = 1000;
        double ThreadRTP = 0;
        double totalBet = bet * numExecution;

        for(int i = 0; i < numExecution; i++){
            Random r = new Random();
            winning = 0;
            double landedNumber = (double) r.nextInt(37);
            // Assuming that evens are blacks and odds are red
            if(landedNumber % 2 != 0 && landedNumber != 0)
                winning = bet*2;
            ThreadRTP += (winning/bet);
        }
        ThreadRTP /= numExecution;
        try{
            // after thread calculates all RTP for the thread, update the main RTP value
            sem.acquire();
            result.RTPTotal = result.RTPTotal + ThreadRTP;
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.println("Thread "+ threadNum + " finished. RTP from this thread: "+ df.format(ThreadRTP * 100)); 
            System.out.println();
            sem.release();
        } catch (InterruptedException ie){
            System.out.println(ie);
        }
        
    }
}

class Results {
    double RTPTotal;
    public Results(){
        this.RTPTotal = 0;
    }

}

