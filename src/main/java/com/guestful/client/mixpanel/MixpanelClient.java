/**
 * Copyright (C) 2013 Guestful (info@guestful.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guestful.client.mixpanel;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class MixpanelClient {

    public static final int MAX_BATCH_SIZE = 50;

    private static final Logger LOGGER = Logger.getLogger(MixpanelClient.class.getName());

    private final Client client;
    private final WebTarget target;
    private boolean enabled = true;
    private boolean batchMode = false;

    public MixpanelClient() {
        this(ClientBuilder.newClient());
    }

    public MixpanelClient(Client restClient) {
        this.client = restClient;
        this.target = buildWebTarget();
    }

    public Client getClient() {
        return client;
    }

    public boolean isBatchMode() {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MixpanelProject getProject(String token) {
        return new MixpanelProject(this, token);
    }

    protected WebTarget buildWebTarget() {
        return getClient().target("http://api.mixpanel.com");
    }

    void batchRequest(String method, String path, JsonArray messages) {
        if (!batchMode && messages.size() > MAX_BATCH_SIZE) {
            throw new MixpanelException("Error sending " + messages.size() + " events: Mixpanel API limited to maximum " + MAX_BATCH_SIZE + " events per call.");
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(method + " " + path + " : " + messages);
        }
        if (batchMode) {
            JsonArrayBuilder batch = Json.createArrayBuilder();
            long bSize = 0;
            for (JsonValue message : messages) {
                batch.add(message);
                bSize++;
                if (bSize % MAX_BATCH_SIZE == 0) {
                    sendBatch(method, path, batch.build());
                    batch = Json.createArrayBuilder();
                }
            }
            if (bSize > 0) {
                sendBatch(method, path, batch.build());
            }
        } else {
            sendBatch(method, path, messages);
        }
    }

    private void sendBatch(String method, String path, JsonArray batch) {
        if (isEnabled()) {
            Response response = target
                .path(path)
                .queryParam("ip", "0")
                .request(MediaType.TEXT_PLAIN)
                .method(method, Entity.entity(
                    new Form("data", Base64.getEncoder().encodeToString(batch.toString().getBytes(StandardCharsets.UTF_8))),
                    MediaType.APPLICATION_FORM_URLENCODED + "; charset=utf-8"));
            String ret = response.readEntity(String.class);
            if (response.getStatus() != 200 || !"1".equals(ret)) {
                throw new MixpanelException(response.getStatus() + " " + response.getStatusInfo().getReasonPhrase() + " " + ret + ". Unable to send Mixpanel tracking events " + batch);
            }
        }
    }

}
