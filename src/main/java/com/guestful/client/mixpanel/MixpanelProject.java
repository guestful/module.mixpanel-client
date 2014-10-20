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
import javax.json.JsonArrayBuilder;
import javax.ws.rs.HttpMethod;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class MixpanelProject {

    private final MixpanelClient client;
    private final String token;

    public MixpanelProject(MixpanelClient client, String token) {
        this.client = client;
        this.token = token;
    }

    public MixpanelClient getClient() {
        return client;
    }

    public String getToken() {
        return token;
    }

    public void track(MixpanelEvent event) {
        track(Arrays.asList(event));
    }

    public void track(MixpanelEvent... events) {
        track(Arrays.asList(events));
    }

    public void track(Collection<MixpanelEvent> events) {
        track(events.stream());
    }

    public void track(Stream<MixpanelEvent> events) {
        Date time = new Date();
        String id = generateDistinctID() + '-';
        long[] incr = {0};
        JsonArrayBuilder messages = Json.createArrayBuilder();
        events.map(mp -> mp.toJson(getToken(), () -> String.valueOf(id + incr[0]++), () -> time)).forEach(messages::add);
        getClient().batchRequest(HttpMethod.POST, "track", messages.build());
    }

    @Override
    public String toString() {
        return token;
    }

    private static String generateDistinctID() {
        UUID j = UUID.randomUUID();
        byte[] data = new byte[16];
        long msb = j.getMostSignificantBits();
        long lsb = j.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            data[i] = (byte) (msb & 0xff);
            msb >>>= 8;
        }
        for (int i = 8; i < 16; i++) {
            data[i] = (byte) (lsb & 0xff);
            lsb >>>= 8;
        }
        return Base64.getEncoder().encodeToString(data);
    }

}
