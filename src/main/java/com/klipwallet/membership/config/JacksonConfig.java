package com.klipwallet.membership.config;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

@JsonComponent
@SuppressWarnings("serial")
public class JacksonConfig {
    /**
     * Long {@literal -json->} string
     * <p>
     * JSON Spec 문제는 없지만 javascript 언어에서 long 표현 못하고 overflow 나는 경우가 있어서 string 변환해야함.
     * </p>
     */

    public static class LongSerializer extends StdSerializer<Long> {
        public LongSerializer() {
            super(Long.class);
        }

        @Override
        public void serialize(@NonNull Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }
    }

    /**
     * string {@literal -json->} Long
     * <p>
     * JSON Spec 문제는 없지만 javascript 언어에서 long 표현 못하고 overflow 나는 경우가 있어서 string 변환해야함.
     * </p>
     */
    public static class LongDeserializer extends StdDeserializer<Long> {
        public LongDeserializer() {
            super(Long.class);
        }

        @Override
        public Long deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            return Optional.ofNullable(parser.getValueAsString())
                           .map(Long::valueOf)
                           .orElse(null);
        }
    }

    /**
     * {@link org.springframework.http.MediaType} {@literal -json->} string
     */
    public static class MediaTypeSerializer extends StdSerializer<MediaType> {
        public MediaTypeSerializer() {
            super(MediaType.class);
        }

        @Override
        public void serialize(@NonNull MediaType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }
    }

    /**
     * string {@literal -json->} {@link org.springframework.http.MediaType}
     */
    public static class MediaTypeDeserializer extends StdDeserializer<MediaType> {
        public MediaTypeDeserializer() {
            super(MediaType.class);
        }

        @Override
        public MediaType deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            return Optional.ofNullable(parser.getValueAsString())
                           .map(MediaType::valueOf)
                           .orElse(null);
        }
    }
}
