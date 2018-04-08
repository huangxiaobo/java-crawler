package com.crawl.pipeline;

import com.crawl.element.User;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserPersistencePipeline implements Pipeline<User> {

    private Logger logger = LoggerFactory.getLogger(UserPrintPipeline.class);

    private PrintWriter out = null;

    public UserPersistencePipeline() {
        try {
            out = new PrintWriter("users.txt");
        } catch (IOException e) {
            e.printStackTrace();
            out = null;
        }
    }


    public void process(User user) {
        out.println(user.toString());
        out.flush();
    }
}
