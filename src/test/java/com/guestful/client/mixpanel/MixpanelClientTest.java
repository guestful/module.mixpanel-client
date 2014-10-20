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

import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Date;
import java.util.logging.LogManager;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@RunWith(JUnit4.class)
public class MixpanelClientTest {

    @Test
    public void test() {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        LoggerFactory.getILoggerFactory();

        Client restClient = ClientBuilder.newBuilder().build();
        restClient.register(JsonProcessingFeature.class);

        MixpanelClient client = new MixpanelClient(restClient);
        client.getProject(System.getenv("MIXPANEL_TOKEN"))
            .track(
                new MixpanelEvent("my-event-1")
                    //.id("my-id")
                    .set("p1", 1)
                    .set("p2", 2),
                new MixpanelEvent("my-event-2")
                    //.id("my-id")
                    .set("p11", 1)
                    .set("p22", 2)
                    .at(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
            );
    }

}
