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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
/**
 *
 * @author dasha
 */
public class MyService2 extends ScheduledService<BlockingQueue>{

    private List<String> sites;

    public MyService2(List<String> sites) {
        this.sites = sites;
    }

    @Override
    protected Task<BlockingQueue> createTask() {
        final Task<BlockingQueue> task;
        task = new Task<BlockingQueue>(){
        
            @Override
            protected BlockingQueue call() throws Exception {          
                BlockingQueue result = new LinkedBlockingQueue<String>();  
                
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                cm.setMaxTotal(100);

                CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
                try{
                    ExecutorService executor = Executors.newFixedThreadPool(sites.size());
                    List<Future<String>> results = new ArrayList<Future<String>>();
                    for (int i = 0; i < sites.size(); i++) {
                       HttpGet httpget = new HttpGet(sites.get(i));
                       Callable worker = new MyCallable(httpclient, httpget);
                       Future<String> res = executor.submit(worker);
                       results.add(res);
                      // String url = hostList[i];
                    //   Runnable worker = new MyRunnable(url);
                    //   executor.execute(worker);
                    //   executor.submit(null);

                   }
                   executor.shutdown();
                   // Wait until all threads are finish
//                   while (!executor.isTerminated()) {
//
//                   }
                   for(Future<String> element:results){
                       result.add(element.get());
                   }
                   System.out.println("\nFinished all threads");    
                   
                } finally{
                    httpclient.close();
                }
                return result;
            }
            
        };
        return task;
    }

  

public static class MyCallable implements Callable {
        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;

        public MyCallable(CloseableHttpClient httpClient, HttpGet httpget) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
        }

        @Override
        public String call() throws Exception {
            String result="";
            try {             
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try {

                    result = Status.ACTIVE.getValue();

                } finally {
                    response.close();
                }
            } catch(HttpHostConnectException e){
                result = Status.INACTIVE.getValue();
            }
            catch (Exception e) {
                System.out.println(" - error: " + e);
            }
            return result;
        }
    
}


    public static class MyCallable2 implements Callable{
        String url;

        public MyCallable2(String url) {
            this.url = url;
        }
        
        @Override
        public String call() throws Exception {
            String result="";
            int code = 200;
            try {
                URL siteURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) siteURL
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
 
                code = connection.getResponseCode();
                if (code == 200) {
                    result = "Green\t";
                }
            } catch (Exception e) {
                result = "->Red<-\t";
            }
            return result;
        }
        
    }
//    static class GetThread extends Thread {
//
//        private final CloseableHttpClient httpClient;
//        private final HttpContext context;
//        private final HttpGet httpget;
//        private final int id;
//        
//        
//        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
//            this.httpClient = httpClient;
//            this.context = new BasicHttpContext();
//            this.httpget = httpget;
//            this.id = id;
//        }
//
//        /**
//         * Executes the GetMethod and prints some status information.
//         */
//        @Override
//        public void run() {
//            try {
//                
//             //   System.out.println(id + " - about to get something from " + httpget.getURI());
//                System.out.print(httpget.getURI()+" ");                
//                CloseableHttpResponse response = httpClient.execute(httpget, context);
//                try {
//                //    System.out.println(id + " - get executed");
//                    // get the response body as an array of bytes
//                  //  HttpEntity entity = response.getEntity();
//                    System.out.println("active");
//
//                } finally {
//                    response.close();
//                }
//            } catch(HttpHostConnectException e){
//                System.out.println("inactive");
//            }
//            catch (Exception e) {
//                System.out.println(id + " - error: " + e);
//            }
//        }
//
//    }

}
