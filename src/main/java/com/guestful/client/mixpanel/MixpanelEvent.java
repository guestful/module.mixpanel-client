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
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.function.Supplier;

public class MixpanelEvent {

    private final String name;
    private String id;
    private Date time;
    private final JsonObjectBuilder properties = Json.createObjectBuilder();

    public MixpanelEvent(String name) {
        this.name = name;
    }

    public JsonObject toJson() {
        return toJson("<token>", () -> "<generated-id>", Date::new);
    }

    public JsonObject toJson(String token, Supplier<String> idProvider, Supplier<Date> dateProvider) {
        return Json.createObjectBuilder()
            .add("event", getName())
            .add("properties", properties
                    .add("distinct_id", getId() != null ? getId() : idProvider.get())
                    .add("time", (getTime() != null ? getTime() : dateProvider.get()).getTime() / 1000)
                    .add("token", token)
                    .build()
            ).build();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public MixpanelEvent id(String id) {
        this.id = id;
        return this;
    }

    public MixpanelEvent at(Date time) {
        this.time = time;
        return this;
    }

    public MixpanelEvent set(String name, String value) {
        if (value == null) return this;
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, BigInteger value) {
        if (value == null) return this;
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, BigDecimal value) {
        if (value == null) return this;
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, int value) {
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, long value) {
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, double value) {
        properties.add(name, value);
        return this;
    }

    public MixpanelEvent set(String name, boolean value) {
        properties.add(name, value);
        return this;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
