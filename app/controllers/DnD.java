package controllers;

import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.HTTP;
import sun.net.www.http.HttpClient;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import canvas.CanvasRequest;
import canvas.SignedRequest;

public class DnD extends Controller {

    public static void index() {
    	for (String aParam : params.all().keySet()) {
    		System.out.println(params.get(aParam));
    	}
        String yourConsumerSecret=System.getenv("CANVAS_CONSUMER_SECRET");
    	CanvasRequest cReq = SignedRequest.verifyAndDecode(params.get("signed_request"), yourConsumerSecret);
    	String jsonQueryResult = doAQuery(cReq);
    	handleSignedRequest();
    	String signedRequest = handleSignedRequest();
    	JsonArray records = (new JsonParser()).parse(jsonQueryResult).getAsJsonObject().getAsJsonArray("records");
        render(signedRequest, records);
    }
    
    private static String doAQuery(CanvasRequest cReq) {
    	String result = "";
    	WSRequest req = WS.url(cReq.getInstanceUrl() + cReq.getContext().getLinkContext().getQueryUrl() + "?q=Select Id, Name From Account");
    	req.setHeader("Authorization", "OAuth " + cReq.getOauthToken());
    	HttpResponse resp = req.get();
    	result = resp.getJson().toString();
    	System.out.println(result);
    	return result;
    }
    
    private static String handleSignedRequest() {
        String signedRequest = params.get("signed_request");
        if (signedRequest == null) {
            System.out.println("This App must be invoked via a signed request!");
            return null;
        }
        String yourConsumerSecret=System.getenv("CANVAS_CONSUMER_SECRET");
        //yourConsumerSecret = "6820991197818332216";
        SignedRequest.verifyAndDecode(signedRequest, yourConsumerSecret);
        String signedRequestJson = SignedRequest.verifyAndDecodeAsJson(signedRequest, yourConsumerSecret);
        System.out.println("JSON Signed Request: \n" + signedRequestJson);
        return signedRequestJson;
    }
}
