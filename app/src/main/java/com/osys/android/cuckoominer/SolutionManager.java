package com.osys.android.cuckoominer;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

/**
 * Created by chris on 07/04/18.
 */

public class SolutionManager {

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
    */
    public static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1000;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Used to update UI with work progress
    private final ThreadPoolExecutor mThreadPoolExecutor;

    private final BlockingQueue<Runnable> mSolutionWorkQueue;

    private Handler mHandler;

    public static int easipct = 50;
    private static int nthreads = 1;
    private static int maxsols = 8;

    private TextView myTextView;
    private int noSolns;
    private int ThreadRef = 0;

    private Performance performance;

    private static SolutionManager sInstance = null;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new SolutionManager();
    }

    private SolutionManager(){

        Log.i("CUCKOOMINER", "SolutionManager");

        mSolutionWorkQueue = new LinkedBlockingQueue<Runnable>();

        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES + 5,   // Initial pool size
                NUMBER_OF_CORES + 8,   // Max pool size
                KEEP_ALIVE_TIME,       // Time idle thread waits before terminating
                KEEP_ALIVE_TIME_UNIT,  // Sets the Time Unit for KEEP_ALIVE_TIME
                mSolutionWorkQueue);  // Work Queue

        mHandler = new Handler(Looper.getMainLooper()){

            /*
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public void handleMessage (Message inputMessage){

                Log.i("CUCKOOMINER", "SolutionManager - handleMessage" + inputMessage.what);
                Solution sol = (Solution) inputMessage.obj;
                myTextView.setText(sol.solution);
                performance.solutionsFound++;

            }
        };
    }

    public static SolutionManager getInstance(){

        Log.i("CUCKOOMINER", "SolutionManager - getInstance");
        return sInstance;
    }

    static public void findSolutions(TextView textViewIn, Performance performanceIn) {

        sInstance.noSolns = 0;
        sInstance.myTextView = textViewIn;
        sInstance.performance = performanceIn;

        Log.i("CUCKOOMINER", "SolutionManager - findSolutions");

        for (int i = 0; i < NUMBER_OF_CORES; i++) {
            Log.i("CUCKOOMINER", "SolutionManager - getInstance - execute");
//               sInstance.mThreadPoolExecutor.execute(new SimpleMiner(i++,new Solution(sInstance.randomHeaderGenerator())));
            sInstance.addToThreadPool();
        }
    }


    private void addToThreadPool(){
        sInstance.mThreadPoolExecutor.execute(new SimpleMiner(ThreadRef++,new Solution(sInstance.randomHeaderGenerator())));
    }

    public void handleState(Solution solution, int state) {

        Log.i("CUCKOOMINER", "SolutionManager - obtainMessage");
        if(solution.solution.equals("1")) {
            mHandler.obtainMessage(state, solution).sendToTarget();
        } else {
            performance.headersTried++;

            if(sInstance.mThreadPoolExecutor.getActiveCount()<NUMBER_OF_CORES){

                Log.i("CUCKOOMINER", "HandleState - active Count tp " + sInstance.mThreadPoolExecutor.getActiveCount()
                        + " Number of Cores " + NUMBER_OF_CORES);
                sInstance.addToThreadPool();
            }
        }
    }


    public byte[] randomHeaderGenerator(){

        byte[] b = new byte[20];
        new Random().nextBytes(b);
        return b;

    }

    public static void cancelAll() {

        /*
         * Creates an array of tasks that's the same size as the task work queue
         */
        SimpleMiner[] taskArray = new SimpleMiner[sInstance.mSolutionWorkQueue.size()];

        // Populates the array with the task objects in the queue
        sInstance.mSolutionWorkQueue.toArray(taskArray);

        // Stores the array length in order to iterate over the array
        int taskArraylen = taskArray.length;

        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (sInstance) {

            // Iterates over the array of tasks
            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

                // Gets the task's current thread
                Thread thread = taskArray[taskArrayIndex].mThreadThis;

                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }

    }

}
