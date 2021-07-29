package actionset;

import com.sakurawald.silicon.Silicon;
import com.sakurawald.silicon.action.abstracts.SourceDetailAction;
import com.sakurawald.silicon.data.beans.SubmitResult;
import com.sakurawald.silicon.data.beans.request.SourceDetailRequest;
import com.sakurawald.silicon.data.beans.response.SourceDetailResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class POJ_SourceDetailAction extends SourceDetailAction {

    @Override
    public SourceDetailResponse execute(SourceDetailRequest sourceDetailRequest) {

        LoggerManager.INSTANCE.logDebug("SourceDetailAction: request = " + sourceDetailRequest);

        SourceDetailResponse sourceDetailResponse = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://poj.org/showsource?solution_id=" + sourceDetailRequest.getRunID())
                .get()
                .addHeader("Cookie", "JSESSIONID=" + sourceDetailRequest.getSubmitAccount().getToken())
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();

            Document doc = Jsoup.parse(response.body().string());

            /** Get SubmitResult. **/
            Elements headInfo_E = doc.select("body > table");
            SubmitResult submitResult = Silicon.getCurrentActionSet().getSubmitResult(headInfo_E.html());

            /** Get Source HTML. **/
            String HTML = doc.html();
            HTML = Silicon.getCurrentActionSet().transferBaseURL(HTML);

            String source = doc.select("body > ul > pre").text();

            if (headInfo_E.isEmpty()) {
                HTML = "NO PERMISSION.";
            }

            sourceDetailResponse = new SourceDetailResponse(sourceDetailRequest.getSubmitAccount(), sourceDetailRequest.getRunID(), HTML, submitResult, source);
            LoggerManager.INSTANCE.logDebug("SourceDetailAction: response = " + response);
            return sourceDetailResponse;
        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }


        throw new RuntimeException("POJ_SourceDetailAction failed.");
    }

    @Override
    public boolean supportAccountClone() {
        return true;
    }

}
