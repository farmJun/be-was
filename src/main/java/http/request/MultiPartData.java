package http.request;

import model.Image;

import java.util.LinkedHashMap;
import java.util.Map;

public class MultiPartData {

    private final Map<String, String> fields = new LinkedHashMap<>();
    private final Map<String, Image> images = new LinkedHashMap<>();

    public Map<String, String> getFields() {
        return fields;
    }

    public Map<String, Image> getImages() {
        return images;
    }

    public void addFile(String name, Image image) {
        images.put(name, image);
    }

    public Image getFile(String key) {
        return images.get(key);
    }

    public void addFormField(String name, String value) {
        fields.put(name, value);
    }

    public String getParameter(String key) {
        return fields.get(key);
    }
}
