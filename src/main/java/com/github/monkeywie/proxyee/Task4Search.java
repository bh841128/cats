package com.github.monkeywie.proxyee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class Task4Search implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(Task4Search.class);

    @Override
    public void run(){

        Vector<String> ret = HttpProxyServerApp.queue4Search.poll();
        if(ret == null||ret.size() == 0){
            return ;
        }

        for(int i = 0; i < ret.size(); i++){
            log.info(ret.get(i));
        }
    }
}
