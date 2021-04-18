package com.epam.deltix.dfp;

import rtmath.utilities.ResourceLoader;

import java.nio.file.Paths;
import java.util.Locale;

public class NativeImplLoader {
    public static void load() {
        try {
            String version = NativeImplLoader.class.getPackage().getImplementationVersion();
            if (version == null)
                version = "0.0.0-SNAPSHOT";
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
