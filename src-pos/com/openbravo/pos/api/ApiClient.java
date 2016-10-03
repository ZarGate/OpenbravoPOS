/*
 * Copyright (C) 2016 filip
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.api;

import com.openbravo.pos.api.model.OrderReceipt;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.openbravo.pos.api.model.ReceiptUploadRequest;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author filip
 */
public class ApiClient {

    private static final Logger LOG = Logger.getLogger(ApiClient.class.getName());

    private final HttpRequestFactory requestFactory;
    private HttpHeaders httpHeaders;
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public ApiClient(String baseUrl, String token, boolean debug) {
        ApiUrl.setBaseUri(baseUrl);
        this.httpHeaders = createHeaders(token, debug);
        requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setHeaders(httpHeaders);
                JsonObjectParser jsonObjectParser = new JsonObjectParser(JSON_FACTORY);
                request.setParser(jsonObjectParser);
            }
        });
    }

    /**
     * Upload order receipt
     *
     * @param ticketId
     * @param content
     */
    public void insertOrUpdateReceipt(String transactionId, OrderReceipt orderReceipt) {
        ApiUrl url = ApiUrl.ReceiptUpload();
        ReceiptUploadRequest uploadRequest = new ReceiptUploadRequest();
        uploadRequest.ticket_id = transactionId;
        uploadRequest.order_receipt = orderReceipt;
        try {
            HttpRequest request = requestFactory.buildPostRequest(url, buildJsonContent(uploadRequest));
            doHttpRequest(request); // Ignore response
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private JsonHttpContent buildJsonContent(Object object) {
        return new JsonHttpContent(JSON_FACTORY, object);
    }

    /**
     * Tries to connect to REST WebService.
     *
     * @return success
     */
    public boolean isReady() {
        Socket socket = null;
        try {
            String hostname = (new URI(ApiUrl.getBaseUri())).getHost();
            socket = new Socket(hostname, 80);
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                }
            }
        }
        return false;
    }

    private HttpResponse doHttpRequest(HttpRequest request) throws IOException, HttpResponseException {
        HttpResponse response;
        response = request.execute();
        if (response.getStatusCode() / 100 == 5) {
            throw new HttpResponseException(response);
        } else {
            return response;
        }
    }

    private HttpHeaders createHeaders(String token, boolean debug) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Custom-Authorization", token);
        headers.set("X-Debug", debug);
        return headers;
    }
}
