/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microservices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thodoris
 */
public class BasicsTest {
    
   
    @Test
    public void references() {
        Runnable run = this::display;
        new Thread(run).start();
    }
    
    void display(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BasicsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void threads() throws InterruptedException{
        List<Thread> pool  = new ArrayList<>();

    }
    
    public String message(){
        return "Hey teo! Time is "+System.nanoTime();
    }
    
    @Test
    public void threadPool() throws InterruptedException{
        ExecutorService tp = Executors.newFixedThreadPool(15);
         for (int i = 0; i < 10000; i++) {
            Runnable run = this::display;
            tp.submit(run);
            Thread.sleep(10);
        }
    }
    
    @Test
    public void callableTest() throws InterruptedException, ExecutionException{
        Callable<String> messageProvider = this::message;
        ExecutorService tp = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Future<String> futureResult = tp.submit(messageProvider);            
            futures.add(futureResult);
        }
        
        for(Future<String> future : futures){
            String result = future.get();
            System.out.println("Future Result = "+result);
        }
    }

    @Test
    public void backPressure(){
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(2);
      
        ThreadPoolExecutor tp = 
                new ThreadPoolExecutor(1, 1 , 60, TimeUnit.SECONDS, 
                        queue, 
                        new ThreadPoolExecutor.CallerRunsPolicy() );
        //Caller Runs Policy forces the client to wait until a thread becomes available.
        //This is also known as "Back Pressure" 
        
        long start = System.currentTimeMillis();
        
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
        
        //One request is processed by the tread (Executor size = 1)
        //Two more requests are accepted and inserted in the queue (queue size = 2)
        //The fourth request has to wait, until a thread becomes available! => Back Pressure!!!
        
    }
    
    @Test
    public void backPressureWithCustomRejectedExecutionHandler(){
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(2);
      
        ThreadPoolExecutor tp = 
                new ThreadPoolExecutor(1, 1 , 60, TimeUnit.SECONDS, 
                        queue, 
                        this::onOverLoad );
        
        long start = System.currentTimeMillis();
       
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
        tp.submit(this::display);
        duration(start);
    }
    public void onOverLoad(Runnable r, ThreadPoolExecutor executor){
        System.out.println("--Runable-- " + r + " executor "+executor.getActiveCount() );
    }
    
    public void duration(long start){
        System.out.println("---- Finished. Took: "+ (System.currentTimeMillis() - start) + " msec");
    }
    
}
