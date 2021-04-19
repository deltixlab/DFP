package com.epam.deltix.dfp;

import rtmath.utilities.ResourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class NativeImplLoader {
    public static void load() {
        try {
            String version = "0.0.0-SNAPSHOT";
            {
                final Class<?> clazz = NativeImplLoader.class;
                final URL clazzResource = clazz.getResource(clazz.getSimpleName() + ".class");
                if (clazzResource != null) {
                    final String classPath = clazzResource.toString();
                    if (classPath.startsWith("jar")) {
                        final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                            "/META-INF/MANIFEST.MF";
                        try (final InputStream manifestStream = new URL(manifestPath).openStream()) {
                            final Manifest manifest = new Manifest(manifestStream);
                            final Attributes attr = manifest.getMainAttributes();
                            version = attr.getValue("Implementation-Version");
                        }
                    }
                }
            }

            final boolean isSnapshot = version.endsWith("-SNAPSHOT");
            if (isSnapshot)
                version = version.substring(0, version.length() - "-SNAPSHOT".length());

            String osName = System.getProperty("os.name");
            if (osName.toLowerCase(Locale.ROOT).contains("windows"))
                osName = "Windows";

            final String osArch = System.getProperty("os.arch");

            ResourceLoader
                .from(NativeImpl.class, osName + "/" + osArch + "/*") // This version now also works, but is probably less efficient
                .to(Paths.get(System.getProperty("java.io.tmpdir"), "com", "EPAM", "Deltix", "DFP", version, osArch).toString())
                .alwaysOverwrite(isSnapshot)
                .tryRandomFallbackSubDirectory(true)
                .load();
        } catch (final Throwable exception) {
            //exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
