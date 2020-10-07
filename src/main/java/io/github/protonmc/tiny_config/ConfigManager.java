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
 * Class used for managing the configuration for a project.
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


    // config file handling

    /**
     * Loads the config into the config field.
     * @throws IOException See Jankson#load
     * @throws SyntaxError See Jankson#load
     */
    public void load() throws IOException, SyntaxError {
        config = Jankson.builder().build().load(Files.newInputStream(path));
    }

    /**
     * Saves some Objects into the configuration file.
     * @param objects The objects to save config entries from.
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
     * Shorthand for loadObject(obj, obj.getSerializedId()).
     * @param obj The object to load the config into.
     */
    public void loadObject(Saveable obj) {
        loadObject(obj, obj.getSerializedId());
    }

    /**
     * Loads a specific part of the config into an object.
     * @param obj The object to load the config into.
     * @param id A unique identifier for the object, specifying the JsonObject to load the config from.
     */
    public void loadObject(Object obj, String id) {
        fromJson(obj, config.getObject(id));
    }


    // converting objects to JsonObjects and vice versa

    /**
     * Gets all of the configurable fields inside a class. Used for loading and saving.
     * @param cl The class to get the fields from.
     * @return A list of configurable fields.
     */
    public static List<Field> getConfigurableFields(Class<?> cl) {
        return Arrays.stream(cl.getFields())
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> f.isAnnotationPresent(Configurable.class))
                .collect(Collectors.toList());
    }

    /**
     * Loads a Json object's entries into an object.
     * @param obj The object to load the entries into.
     * @param json The JsonObject to load the entries from.
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
     * Saves the configurable fields of an Object in a JsobObject.
     * @param obj The object to save its entries.
     * @return The JsobObject with the saved entries.
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
