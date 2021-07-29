package actionset;

import com.sakurawald.silicon.action.abstracts.ProblemsAction;
import com.sakurawald.silicon.data.beans.Page;
import com.sakurawald.silicon.data.beans.Problem;
import com.sakurawald.silicon.data.beans.request.ProblemsRequest;
import com.sakurawald.silicon.data.beans.response.ProblemsResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import com.sakurawald.silicon.util.PluginUtil;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class POJ_ProblemsAction extends ProblemsAction {

    public ProblemsResponse searchProblem(ProblemsRequest problemsRequest) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        String encodeSearchKey = problemsRequest.getProblemSearchKey();

        RequestBody body = RequestBody.create(mediaType, "key=" + encodeSearchKey + "&field=title&B1=GO");
        Request request = new Request.Builder()
                .url("http://poj.org/searchproblem")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", "JSESSIONID=" + problemsRequest.getRequestAccount().getToken())
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        ArrayList<Problem> problemList = null;
        try {
            response = client.newCall(request).execute();

            /** HTML Analyse. **/
            String HTML = response.body().string();
            problemList = PluginUtil.INSTANCE.fastGetProblemList(HTML,
                    "body > center:nth-child(5) > table > tbody > tr", 5,
                    0, 0, 1,
                    -1, -1, -1);

        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }


        return new ProblemsResponse(problemList);
    }

    public ProblemsResponse problemList(ProblemsRequest problemsRequest) {

        if (Page.HOME_PAGE.equals(problemsRequest.getPage())) problemsRequest.setPage(Page.FIRST_PAGE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://poj.org/problemlist?volume=" + problemsRequest.getPage())
                .get()
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cookie", "JSESSIONID=" + problemsRequest.getRequestAccount().getToken())
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;

        ArrayList<Problem> problemList = null;
        try {
            response = client.newCall(request).execute();

            /** HTML Analyse. **/
            Document doc = Jsoup.parse(response.body().string());
            problemList = PluginUtil.INSTANCE.fastGetProblemList(doc.html(), "body > table.a > tbody > tr",
                    5, 0, 0, 1, 2, -1, 3);

        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }


        return new ProblemsResponse(problemList);
    }


    @Override
    public ProblemsResponse execute(ProblemsRequest problemsRequest) {

        LoggerManager.INSTANCE.logDebug("ProblemsAction: request = " + problemsRequest);

        // Is Search Problem ?
        if (problemsRequest.getProblemSearchKey() == null) {
            return problemList(problemsRequest);
        } else {
            return searchProblem(problemsRequest);
        }

    }

    @Override
    public boolean supportProblemSearch() {
        return true;
    }


}
