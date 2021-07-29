package actionset;

import com.sakurawald.silicon.action.abstracts.SubmitAction;
import com.sakurawald.silicon.data.beans.request.SubmitRequest;
import com.sakurawald.silicon.data.beans.response.SubmitResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class POJ_SubmitAction extends SubmitAction {

    @Override
    public SubmitResponse execute(SubmitRequest submitRequest) {

        LoggerManager.INSTANCE.logDebug("SubmitAction: " + submitRequest);

        String code = submitRequest.getCode();
        code = Base64.getEncoder().encodeToString(code.getBytes());
        submitRequest.setCode(code);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "problem_id=" + submitRequest.getProblemID() + "&language=" + submitRequest.getLanguage() + "&source=" + submitRequest.getCode() + "&submit=Submit&encoded=1");
        Request request = new Request.Builder()
                .url("http://poj.org/submit")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", "JSESSIONID=" + submitRequest.getSubmitAccount().getToken())
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }


        LoggerManager.INSTANCE.logDebug("SubmitAction: response = " + response);

        return new SubmitResponse();
    }


}
