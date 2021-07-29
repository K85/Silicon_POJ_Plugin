package actionset;

import com.sakurawald.silicon.Silicon;
import com.sakurawald.silicon.action.abstracts.ProblemDetailAction;
import com.sakurawald.silicon.data.beans.request.ProblemDetailRequest;
import com.sakurawald.silicon.data.beans.response.ProblemDetailResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class POJ_ProblemDetailAction extends ProblemDetailAction {


    @Override
    public ProblemDetailResponse execute(ProblemDetailRequest problemDetailRequest) {

        LoggerManager.INSTANCE.logDebug("ProblemDetail: request = " + problemDetailRequest);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://poj.org/problem?id=" + problemDetailRequest.getProblem().getProblemID())
                .get()
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();


        Response response = null;

        try {
            response = client.newCall(request).execute();

            /** Get ProblemDetail HTML. **/
            String HTML = response.body().string();

            Document doc = Jsoup.parse(HTML);
            Elements doc_elements = doc.select("body > table:nth-child(3) > tbody");
            HTML = doc.select("head").outerHtml() + doc_elements.outerHtml();

            // Transfer BaseURL.
            HTML = Silicon.getCurrentActionSet().transferBaseURL(HTML);
            ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse(problemDetailRequest.getProblem(), HTML);
            LoggerManager.INSTANCE.logDebug("ProblemDetailAction: response = " + problemDetailResponse);
            return problemDetailResponse;
        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }

        throw new RuntimeException("POJ_ProblemDetailAction Failed.");
    }

}
