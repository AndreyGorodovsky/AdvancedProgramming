package test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String confFilePath;
    private final List<ParallelAgent> created = new ArrayList<>();

    public void setConfFile(String path) {
        this.confFilePath = path;
    }

    @Override
    public void create() {
        List<String> raw;
        try {
            raw = Files.readAllLines(Paths.get(confFilePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file: " + confFilePath, e);
        }

        List<String> lines = new ArrayList<>();
        for (String L : raw) {
            String t = L.trim();
            if (!t.isEmpty()) lines.add(t);
        }

        if (lines.size() % 3 != 0) {
            return;
        }

        for (int i = 0; i < lines.size(); i += 3) {
            String className = lines.get(i);
            String[] subs = lines.get(i + 1).split("\\s*,\\s*");
            String[] pubs = lines.get(i + 2).split("\\s*,\\s*");
            try {
                Class<?> cls = Class.forName(className);
                Constructor<?> ctor = cls.getConstructor(String[].class, String[].class);
                Agent rawAgent = (Agent) ctor.newInstance((Object) subs, (Object) pubs);
                ParallelAgent pa = new ParallelAgent(rawAgent, 16);
                created.add(pa);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to instantiate agent from config: " + className, e);
            }
        }
    }

    @Override
    public String getName() {
        return "GenericConfig(" + confFilePath + ")";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        // Shutdown threads in reverse order
        for (int i = created.size() - 1; i >= 0; i--) {
            created.get(i).close();
        }
        created.clear();
    }
}
