package com.staysync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {

    public static <T> void save(List<T> data, String filePath) {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> load(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(f))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Load failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}