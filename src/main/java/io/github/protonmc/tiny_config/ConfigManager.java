package io.github.protonmc.tiny_config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a config file; has static functions handling object serialization.
 * @author dzwdz
 */
public class ConfigManager {
    private final Path path;
    public JsonObject config = new JsonObject();

    /**
     * Constructs a ConfigManager object.
     * @param path The file where the configuration is stored.
     */
    public ConfigManager(Path path) {
        this.path = path;
    }

    /**
     * Reads the config.
     * @throws IOException See Jankson#load
     * @throws SyntaxError See Jankson#load
     */
    public void load() throws IOException, SyntaxError {
        config = Jankson.builder().build().load(Files.newInputStream(path));
    }


    /**
     * Saves the state of some Saveables to disk.
     * @param objects The Saveables to save.
     * @throws IOException See Writer#write.
     */
    public void save(Iterable<? extends Saveable> objects) throws IOException {
        for (Saveable o : objects) {
            config.put(o.getSerializedId(), toJson(o));
        }
        BufferedWriter writer = Files.newBufferedWriter(path);
        writer.write(config.toJson(true, true));
        writer.close();
    }

    /**
     * Populates the @Configurable fields of a Saveable from the currently loaded config.
     * @param obj The Saveable to modify.
     */
    public void loadObject(Saveable obj) {
        loadObject(obj, obj.getSerializedId());
    }

    /**
     * Populates the @Configurable fields of an object from the currently loaded config.
     * @param obj The object to modify.
     * @param id A unique identifier for the object, specifying the JsonObject to load the config from.
     */
    public void loadObject(Object obj, String id) {
        fromJson(obj, config.getObject(id));
    }


    /**
     * Gets all of the @Configurable fields inside of a class. Used for loading and saving.
     * @param cl The class to get the fields from.
     * @return A list of the @Configurable fields.
     */
    public static List<Field> getConfigurableFields(Class<?> cl) {
        return Arrays.stream(cl.getFields())
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> f.isAnnotationPresent(Configurable.class))
                .collect(Collectors.toList());
    }

    /**
     * Populates the @Configurable fields of an object from a JsonObject.
     * @param obj The object to modify.
     * @param json The JsonObject to load the fields from.
     */
    public static void fromJson(Object obj, JsonObject json) {
        if (json == null) return;
        for (Field f : getConfigurableFields(obj.getClass())) {
            try {
                f.set(obj,
                        json.getMarshaller().marshall(f.getType(), json.get(f.getName())));
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Saves the @Configurable fields of an Object into a JsonObject.
     * @param obj The object that will have its fields saved.
     * @return The JsonObject with the fields.
     */
    public static JsonObject toJson(Object obj) {
        JsonObject json = new JsonObject();
        for (Field f : getConfigurableFields(obj.getClass())) {
            try {
                json.put(f.getName(), json.getMarshaller().serialize(f.get(obj)));
            } catch (Throwable ignored) {}
        }
        return json;
    }
}
