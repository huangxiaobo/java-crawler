package com.huangxiaobo.crawler.common;

public class Constants {

  public static final int TIMEOUT = 10000;
  /**package com.huangxiaobo.crawler.crawler.common;
   * 单个ip请求间隔，单位ms
   */

  // 知乎首页
  public static final String INDEX_URL = "https://www.zhihu.com";

  public static final String USER_FOLLOWEES_URL =
      "https://www.zhihu.com/api/v4/members/%s/followees?"
          + "include=data[*].educations,employments,answer_count,business,locations,articles_count,follower_count,"
          + "gender,following_count,question_count,voteup_count,thanked_count,is_followed,is_following,"
          + "badge[?(type=best_answerer)].topics&offset=%d&limit=20";
  public static final String USER_ANSWER_URL =
      "https://www.zhihu.com/api/v4/members/%s/answers?"
          + "include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,"
          + "annotation_detail,collapse_reason,collapsed_by,suggest_edit,comment_count,can_comment,content,"
          + "voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,review_info,"
          + "question,excerpt,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,upvoted_followees;"
          + "data[*].author.badge[?(type=best_answerer)].topics&offset=%d&limit=20&sort_by=created";

  public static final String[] userAgentArray =
      new String[]{
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2623.110 Safari/537.36",
          "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:50.0) Gecko/20100101 Firefox/50.0",
          "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36"
      };

  public static final String MQ_QUEUE_NAME = "zhihu.user.url.queue";
  public static final String MQ_EXCHANGE_NAME = "zhihu.user.url.exchange";
  public static final String MQ_ROUTING_KEY = "zhihu.user.url.bindingkey";

  public static final String MQ_PAGE_QUEUE_NAME = "zhihu.user.page.queue";
  public static final String MQ_PAGE_EXCHANGE_NAME = "zhihu.user.page.exchange";
  public static final String MQ_PAGE_ROUTING_KEY = "zhihu.user.page.bindingkey";

  public static final String MQ_JSON_QUEUE_NAME = "zhihu.user.json.queue";
  public static final String MQ_JSON_EXCHANGE_NAME = "zhihu.user.json.exchange";
  public static final String MQ_JSON_ROUTING_KEY = "zhihu.user.json.bindingkey";

  public static final String USER_DETAIL_PARSER_CLASS_NAME = "UserDetailParser";
}
