package com.crawl.task;

import com.crawl.element.User;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/5.
 */
public class UserPersistenceTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UserPersistenceTask.class);

    private User user;

    private static int count = 0;

    private static PrintWriter out = null;

    static {
        try {
            out = new PrintWriter("users.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserPersistenceTask(User user) {

        this.user = user;
    }


    public void run() {
        if (user == null) {
            return;
        }
        logger.info("hxb -------out" + count++ + " user:" + user.toString());

        String content = user.toString();
        out.println(content);
        out.flush();
        logger.info("out: " + content);
    }
}
