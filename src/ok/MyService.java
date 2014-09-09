/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ok;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author dasha
 */
public class MyService extends ScheduledService<ObservableList<Site>>{

    private ObservableList<Site> sites;

    public MyService(ObservableList<Site> sites) {
        this.sites = sites;
    }

    @Override
    protected Task<ObservableList<Site>> createTask() {
        final Task<ObservableList<Site>> task = new Task<ObservableList<Site>>(){

            @Override
            protected ObservableList<Site> call() throws Exception {
//                ExecutorService executor = Executors.newFixedThreadPool(10);   
//
//                LinkedList<Future<String>> result = new LinkedList<Future<String>>();
//                for (Site site:sites){
//                    Callable<String> callable = new MyCallable(site.getName());
//                    Future<String> future = executor.submit(callable);
//                    result.add(future);
//                }     
//                executor.shutdown();
//                while (!executor.isTerminated()) {
//
//                }
//                for(Site site:sites){
//                    site.setStatus(result.poll().get());
//                }
                
                
                
                Random rand = new Random();
                for (Site site : sites) {
                    if(rand.nextBoolean()){
                        site.setStatus(Status.ACTIVE.getValue());
                    } else {
                        site.setStatus(Status.INACTIVE.getValue());
                    }
                    
                }
                return null;
            }
            
        };
        return task;
    }

    
    public class MyCallable implements Callable{
        String url;

        public MyCallable(String url) {
            this.url = url;
        }
        
        @Override
        public String call() throws Exception {
            String result="";
            int code = 200;
            HttpURLConnection connection = null;
            try {
                URL siteURL = new URL(url);
                connection = (HttpURLConnection) siteURL
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
 
                code = connection.getResponseCode();
                if (code == 200) {
                    result = Status.ACTIVE.getValue();
                }
            } catch (Exception e) {
                result = Status.INACTIVE.getValue();
            } finally{
                connection.disconnect();
            }
            return result;
        }
        
    }
}

