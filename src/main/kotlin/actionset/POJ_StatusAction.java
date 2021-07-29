package actionset;

import com.sakurawald.silicon.action.abstracts.StatusAction;
import com.sakurawald.silicon.data.beans.Page;
import com.sakurawald.silicon.data.beans.request.StatusRequest;
import com.sakurawald.silicon.data.beans.response.StatusResponse;
import com.sakurawald.silicon.data.beans.response.SubmitResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import com.sakurawald.silicon.util.PluginUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class POJ_StatusAction extends StatusAction {

    /**
     * 存储Prev Page 和 Next Page的参数.
     **/
    private static String prevPageParam = null;
    private static String nextPageParam = null;

    @Override
    public StatusResponse execute(StatusRequest requestBean) {

        LoggerManager.INSTANCE.logDebug("StatusAction: request = " + requestBean);

        ArrayList<SubmitResponse> submitResponseList = null;

        /** Calc UserID & ProblemID. **/
        if (requestBean.getUserID() == null) requestBean.setUserID("");
        if (requestBean.getProblemID() == null) requestBean.setProblemID("");

        /** Calc Top. **/
        String pageControl = requestBean.getPage();
        if (pageControl.equals(Page.HOME_PAGE)) pageControl = "";
        if (pageControl.equals(Page.PREV_PAGE) && prevPageParam != null)
            pageControl = "bottom=" + prevPageParam;
        if (pageControl.equals(Page.NEXT_PAGE) && nextPageParam != null) pageControl = "top=" + nextPageParam;


        OkHttpClient client = new OkHttpClient();
        String URL = "http://poj.org/status?problem_id=" + requestBean.getProblemID() + "&user_id=" + requestBean.getUserID() + "&result=&language=" + "&" + pageControl;
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cookie", "JSESSIONID=" + requestBean.getRequestAccount().getToken())
                .addHeader("Connection", "keep-alive")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();

            /** HTML Analyse. **/
            Document doc = Jsoup.parse(response.body().string());
            String HTML = doc.html();

            /** PrevPageButton & NextPageButton. **/
            Elements pageButtons = doc.select("body > p:nth-child(6)");
            Elements prevButton = pageButtons.select("a:nth-child(2)");
            Elements nextButton = pageButtons.select("a:nth-child(3)");
            String prevButtonString = prevButton.attr("href");
            String nextButtonString = nextButton.attr("href");

            prevPageParam = prevButtonString.substring(prevButtonString.lastIndexOf("=") + 1);
            nextPageParam = nextButtonString.substring(nextButtonString.lastIndexOf("=") + 1);

            /** Status. **/
            submitResponseList = PluginUtil.INSTANCE.fastGetSubmitResponseList(HTML,
                    "body > table.a > tbody > tr",
                    0, 1, 2, 3, 4, 5, 6, 7, 8);

        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }

        LoggerManager.INSTANCE.logDebug("StatusAction: response = " + response);
        return new StatusResponse(submitResponseList);
    }

    @Override
    public boolean supportStatusPageSkip() {
        return false;
    }

}
