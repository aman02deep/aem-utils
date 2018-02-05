package com.aem.utils.core.utils;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
    private HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

    public GetMethod getDefaultGetMethod(String url, String queryParam) {
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset("UTF-8");
        GetMethod getMethod = new GetMethod();
        getMethod.setPath(url);
        if (StringUtils.isNotBlank(queryParam)) {
            getMethod.setQueryString(queryParam);
        }
        getMethod.setParams(params);
        return getMethod;
    }

    /**
     * @param getMethod
     * @return Response String This method Can return
     *          1. Empty : When Service Url is not correct
     *          2. Error : When there is any Exception or GetMethod is null
     *          3. Server Response
     **/
    public String callExternalServiceGet(GetMethod getMethod) {
        if (Objects.isNull(getMethod)) {
            LOGGER.debug("Incorrect or Empty getMethod");
            return "Error";
        }

        Credentials defaultcreds = new UsernamePasswordCredentials("admin", "admin");
        client.getState().setCredentials(new AuthScope("localhost", 4502, AuthScope.ANY_REALM), defaultcreds);

        try {
            LOGGER.debug("Calling Service : {} ", getMethod.getPath());
            if ("/".equals(getMethod.getPath())) {
                return "";
            }
            int statusCode = client.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Http Client execute Status is Not Ok, response status is : {}", statusCode);
                return "";
            }
            return getMethod.getResponseBodyAsString();
        } catch (IOException e) {
            LOGGER.error("Exception occured", e);
            return "Error";
        } finally {
            getMethod.releaseConnection();
        }
    }

    /**
     * @param client
     *            the client to set
     */
    public void setClient(HttpClient client) {
        this.client = client;
    }

}

