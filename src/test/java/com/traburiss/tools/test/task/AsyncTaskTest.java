package com.traburiss.tools.test.task;

import com.traburiss.tools.task.AsyncTask;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * com.traburiss.tools.task
 *
 * @author ltc
 * @version 1.0.0.1
 * create at 2018/4/27 10:49
 */

public class AsyncTaskTest {

    private Logger logger = Logger.getLogger(AsyncTaskTest.class.getName());

    @Test
    @DisplayName("线程池测试")
    public void taskTest(){

        logger.info("start task test");
        int length = 1000;
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < length; i++) {

            boolean haveFree = Task.parallelSuitable();
            if (haveFree){

                Task task = new Task(i, list, length);
                task.execute();
            }
        }
    }

    public class Task extends AsyncTask {

        int i;
        List<Integer> list;
        int length;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss.sss");

        Task(int i, List<Integer> list, int length){

            this.i = i;
            this.list = list;
            this.length = length;
        }

        @Override
        public void doInNewThread() {

            Date date = new Date();
            try {
                float d =(float)(length - i)/(float) length;
                long time = (long) (1000 * d);
                Thread.sleep(time);
//                Thread.sleep((long) (Math.random()*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(i + ":" + format.format(date) + "\t" + format.format(new Date()));
            list.add(i);
            if (list.size() >= length){

                for (int j = 0; j < 100; j++) {

                    logger.info("" + list.size());
                }
            }
        }
    }
}
