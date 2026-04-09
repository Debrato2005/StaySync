package com.staysync.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {

    private static final Path DATA_DIR = Paths.get("data");

    private static void ensureDataDir() {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            System.err.println("Could not create data dir: " + e.getMessage());
        }
    }

    public static <T> void save(List<T> data, String fileName) {
        ensureDataDir();
        Path file = DATA_DIR.resolve(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file.toFile()))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Save failed [" + fileName + "]: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> load(String fileName) {
        Path file = DATA_DIR.resolve(fileName);
        if (!Files.exists(file)) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file.toFile()))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Load failed [" + fileName + "]: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}