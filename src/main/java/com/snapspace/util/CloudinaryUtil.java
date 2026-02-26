package com.snapspace.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Utility class that owns all Cloudinary SDK knowledge.
 *
 * <p>
 * No other class in the app imports or references the Cloudinary SDK directly.
 * Everything goes through the static methods on this class.
 * </p>
 *
 * <p>
 * Uses the same "initialization-on-demand holder" pattern as {@link HibernateUtil}
 * — thread-safe lazy singleton, credentials loaded from {@code config.properties}
 * via {@link PropertiesUtil}.
 * </p>
 *
 * <p>
 * Node.js equivalent: this is like wrapping the entire cloudinary npm package
 * in your own module — the rest of the app only ever calls your wrapper,
 * never the library directly.
 * </p>
 */
public class CloudinaryUtil {

    /**
     * Private constructor — utility class, never instantiated.
     */
    private CloudinaryUtil() {
    }

    /**
     * Inner holder — JVM guarantees this class is only loaded once,
     * giving us a thread-safe singleton without any synchronization overhead.
     */
    private static class Holder {
        private static final Cloudinary INSTANCE = buildCloudinary();
    }

    /**
     * Reads Cloudinary credentials from {@code config.properties} and
     * builds the SDK instance. Called exactly once by the Holder.
     *
     * @return a configured {@link Cloudinary} instance
     * @throws ExceptionInInitializerError if any credential is missing
     */
    private static Cloudinary buildCloudinary() {
        String cloudName = PropertiesUtil.get("cloud_name");
        String apiKey = PropertiesUtil.get("api_key");
        String apiSecret = PropertiesUtil.get("api_secret");

        if (cloudName == null || apiKey == null || apiSecret == null) {
            throw new ExceptionInInitializerError("Cloudinary credentials missing from config.properties. " + "Expected: cloud_name, api_key, api_secret");
        }

        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret, "secure", true));
    }

    /**
     * Uploads raw bytes to Cloudinary and returns the result map.
     *
     * <p>
     * This is the only method other classes call. They pass bytes and a folder,
     * get back a plain {@link Map} with keys like {@code secure_url} and
     * {@code public_id} — no Cloudinary types leak out.
     * </p>
     *
     * <p>
     * Node.js equivalent:
     * {@code const result = await cloudinary.uploader.upload(buffer, { folder })}
     * </p>
     *
     * @param bytes  the raw image bytes to upload
     * @param folder the Cloudinary folder to organize uploads, e.g. "snapspace/user_3"
     * @return the Cloudinary response map containing secure_url, public_id, etc.
     * @throws IOException if the upload fails
     */
    public static Map upload(byte[] bytes, String folder) throws IOException {
        return Holder.INSTANCE.uploader().upload(bytes, ObjectUtils.asMap("folder", folder));
    }
}