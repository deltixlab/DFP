package com.epam.deltix.dfp;

import rtmath.utilities.ResourceLoader;

import java.nio.file.Paths;

public class NativeImplLoader {
    public static void load() {
        try {
            final String archName = "x" + System.getProperty("sun.arch.data.model");
            String version = NativeImplLoader.class.getPackage().getImplementationVersion();
            if (version == null)
                version = "0.0.0-SNAPSHOT";
            final boolean isSnapshot = version.endsWith("-SNAPSHOT");
            if (isSnapshot)
                version = version.substring(0, version.length() - "-SNAPSHOT".length());
            ResourceLoader
                .from(NativeImpl.class, "$(OS)/$(ARCH)/*") // This version now also works, but is probably less efficient
                //.from(NativeImplLoader.class, "$(OS)/$(ARCH)/DecimalNative.$(DLLEXT).zst")
                .to(Paths.get(System.getProperty("java.io.tmpdir"), "com", "EPAM", "Deltix", "DFP", version, archName).toString())
                // .lowercasePathOnLinux(false)
                .alwaysOverwrite(isSnapshot)
                .tryRandomFallbackSubDirectory(true)
                .load();
        } catch (final Throwable exception) {
            //exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}
