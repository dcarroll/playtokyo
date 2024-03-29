/**
 * Copyright (c) 2011, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package canvas;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * The canvas request is what is sent to the client on the very first request. In this canvas
 * request is information for authenticating and context about the user, organization and environment.
 * <p>
 * This class is serialized into JSON on then signed by the signature service to prevent tampering.
 *
 */
//@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasRequest {

    private String  algorithm;
    private Integer issuedAt;
    private String  userId;
    private String  oauthToken;
    private Map<String,Object> params;
    private String  clientId;
    private String  instanceUrl;
    private CanvasContext canvasContext;
    private String jsonRequest;
    
    
    private void processJsonRequest(String json) {
        // Deserialize the json body
    	this.jsonRequest = new String(new Base64(true).decode(json));
        Gson g = new Gson();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonRequest);
        JsonObject canvRequst = je.getAsJsonObject();
        JsonObject context = canvRequst.getAsJsonObject("context");
        JsonObject otherContext = context.getAsJsonObject("user");
        
        CanvasRequest canvasRequest = g.fromJson(canvRequst, CanvasRequest.class) ;
        this.algorithm = canvasRequest.algorithm;
        this.issuedAt = canvasRequest.issuedAt;
        this.userId = canvasRequest.userId;
        this.oauthToken = canvasRequest.oauthToken;
        this.params = canvasRequest.params;
        this.clientId = canvasRequest.clientId;
        this.instanceUrl = canvasRequest.instanceUrl;
        
        this.setContext(g.fromJson(context, CanvasContext.class));
        this.getContext().setUserContext(g.fromJson(otherContext, CanvasUserContext.class));
        otherContext = context.getAsJsonObject("links");
        this.getContext().setLinkContext(g.fromJson(otherContext, CanvasLinkContext.class));
        otherContext = context.getAsJsonObject("organization");
        this.getContext().setOrganizationContext(g.fromJson(otherContext, CanvasOrganizationContext.class));
        otherContext = context.getAsJsonObject("environment");
        this.getContext().setEnvironmentContext(g.fromJson(otherContext, CanvasEnvironmentContext.class));
    }
    
    /**
     * The Canvas request as a jsonString
     */
    public String getJsonRequest() {
        return jsonRequest;
    }

    public void setJsonRequest(String jsonRequest) {
    	processJsonRequest(jsonRequest);
    }

    /**
     * The algorithm used to sign the request. typically HMAC-SHA256
     * @see platform.connect.service.SignRequestService.ALGORITHM
     */
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * The unix time this request was issued at.
     */
    public Integer getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Integer issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * The Salesforce unique id for this user.
     * @return
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * The scoped OAuth token to be used to subsequent REST calls
     */
    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String OAuthToken) {
        this.oauthToken = OAuthToken;
    }

    /**
     * URL Parameters, put here to prevent tampering
     */
    public Map<String,Object> getParameters() {
        if (null == this.params){
            this.params = new HashMap<String, Object>();
        }
        return params;
    }

    public void setParameters(Map<String,Object> params) {
        this.params = params;
    }

    /**
     * The Connect app client key.
     * @REVIEW: should we be sending this. they should already have this?
     * @return
     */
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Context information about the user, org and environment.
     */
    public CanvasContext getContext() {
        return canvasContext;
    }

    public void setContext(CanvasContext canvasContext) {
        this.canvasContext = canvasContext;
    }

    /**
     * The base url for all subsequent REST call, this has the correct
     * Salesforce instance this organization is pinned to.
     */
    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
}
