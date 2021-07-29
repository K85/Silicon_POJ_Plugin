package actionset;

import com.sakurawald.silicon.action.abstracts.LoginAction;
import com.sakurawald.silicon.data.beans.request.LoginRequest;
import com.sakurawald.silicon.data.beans.response.LoginResponse;
import com.sakurawald.silicon.debug.LoggerManager;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class POJ_LoginAction extends LoginAction {


    public LoginResponse execute(LoginRequest loginRequest) {

        LoggerManager.INSTANCE.logDebug("LoginAction: " + loginRequest);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "user_id1=" + loginRequest.getUserID() + "&password1=" + loginRequest.getPassword() + "&B1=login");
        Request request = new Request.Builder()
                .url("http://poj.org/login")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .build();

        String token = null;
        String loginMessage = null;

        Response response = null;
        try {
            response = client.newCall(request).execute();
            token = super.getLoginToken(response, "JSESSIONID");
            loginMessage = super.getLoginMessage(response);

            LoggerManager.INSTANCE.logDebug("Get Token: " + token);
        } catch (IOException e) {
            LoggerManager.INSTANCE.reportException(e);
        } finally {
            Objects.requireNonNull(response.body()).close();
        }


        return new LoginResponse(token);
    }

}
