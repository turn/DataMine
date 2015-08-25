package datamine.storage.idl.generator.metadata;

import com.google.gson.*;
import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.Schema;
import datamine.storage.idl.type.*;

import java.lang.reflect.Type;

/**
 * Convert String to Schema
 * Created by tliu on 8/24/15.
 */
public class SchemaStringConverter implements UnaryOperatorInterface<String, Schema> {

    @Override
    public Schema apply(String s) {
        Gson gson = new GsonBuilder().registerTypeAdapter(FieldType.class, new FieldDeserializer()).create();
        return gson.fromJson(s, Schema.class);
    }

    //helper class for Gson to convert from string to schema
    public static class FieldDeserializer implements JsonDeserializer<FieldType> {

        @Override
        public FieldType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonObject().has("type")) {
                String type = json.getAsJsonObject().get("type").getAsString();
                PrimitiveType pType = PrimitiveType.valueOf(type);
                return new PrimitiveFieldType(pType);
            }
            if (json.getAsJsonObject().has("collectionType")) {
                CollectionType cType = CollectionType.valueOf(json.getAsJsonObject().get("collectionType").getAsString());
                FieldType elemType = deserialize(json.getAsJsonObject().get("elementType"), typeOfT, context);
                return new CollectionFieldType(elemType, cType);
            }
            if (json.getAsJsonObject().has("groupName")) {
                return new GroupFieldType(json.getAsJsonObject().get("groupName").getAsString(), null);
            }
            throw new RuntimeException("not a valid type" + json.toString());
        }
    }
}
