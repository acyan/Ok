/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ok;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author dasha
 */
public class TimerService extends ScheduledService<Integer>{

    Integer i;

    public void setI(Integer i) {
        this.i = i;
    }

    public TimerService(int i) {
        this.i = i;
    }
    
    @Override
    protected Task<Integer> createTask() {
        final Task<Integer> task;
        task = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {
                
                return i++;
            }
            
        };
        return task;
    }
    
    
}
