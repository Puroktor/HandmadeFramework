package ru.vsu.csf.skofenko.server.dockerlogic.di;

import ru.vsu.csf.skofenko.server.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ContextLoader {

    public static Collection<Class<?>> getAllClasses(JarFile jar) {
        Collection<Class<?>> classes = new ArrayList<>();
        URLClassLoader loader;
        try {
            loader = new URLClassLoader(new URL[]{new URL("jar:file:" + jar.getName() + "!/")});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        File destDir = getResourceFile(jar);
        byte[] buffer = new byte[1024];
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.startsWith("static/")) {
                try {
                    File newFile = newFile(destDir, entry);
                    if (!entry.isDirectory()) {
                        FileOutputStream fos = new FileOutputStream(newFile);
                        InputStream is = jar.getInputStream(entry);
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (file.endsWith(".class")) {
                String classname = file.replace('/', '.').substring(0, file.length() - ".class".length());
                try {
                    Class<?> clas = loader.loadClass(classname);
                    classes.add(clas);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Failed to instantiate " + classname + " from " + file, e);
                }
            }
        }
        return classes;
    }

    public static File getResourceFile(JarFile jar) {
        String jarName = Paths.get(jar.getName()).getFileName().toString();
        jarName = jarName.substring(0, jarName.length() - ".jar".length());
        return new File("resources/" + jarName);
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName().substring(
                Application.DOCKER_PATH.length() + 1));
        if (zipEntry.isDirectory()) {
            destFile.mkdirs();
        }
        return destFile;
    }
}
