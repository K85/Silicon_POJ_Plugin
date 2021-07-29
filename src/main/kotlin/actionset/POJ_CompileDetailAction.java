package actionset;

import com.sakurawald.silicon.action.abstracts.CompileDetailAction;
import com.sakurawald.silicon.data.beans.request.CompileDetailRequest;
import com.sakurawald.silicon.data.beans.response.CompileDetailResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class POJ_CompileDetailAction extends CompileDetailAction {

    @Override
    public CompileDetailResponse execute(CompileDetailRequest compileDetailRequest) {

        LoggerManager.INSTANCE.logDebug("CompileDetailAction: request = " + compileDetailRequest);

        CompileDetailResponse compileDetailResponse = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://poj.org/showcompileinfo?solution_id=" + compileDetailRequest.getRunID())
                .get()
                .addHeader("Cookie", "JSESSIONID=" + compileDetailRequest.getSubmitAccount().getToken())
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();

            /** Get Body HTML. **/
            Document doc = Jsoup.parse(response.body().string());
            String HTML = doc.html();

            if (HTML.contains("No such solution")) {
                HTML = "COMPILE SUCCESSFULLY!";
            }

            compileDetailResponse = new CompileDetailResponse(compileDetailRequest.getSubmitAccount(), compileDetailRequest.getRunID(), HTML);
            LoggerManager.INSTANCE.logDebug("CompileDetailAction: response = " + response);
            return compileDetailResponse;
        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }

        throw new RuntimeException("POJ_CompileDetailAction failed.");
    }

}
