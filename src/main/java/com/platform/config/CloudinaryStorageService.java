package com.platform.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Authenticated Cloudinary storage; application controllers remain the authorization gate. */
@Service
public class CloudinaryStorageService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorageService.class);
    private final Cloudinary cloudinary;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public CloudinaryStorageService(@Value("${CLOUDINARY_CLOUD_NAME:}") String name,
                                    @Value("${CLOUDINARY_API_KEY:}") String key,
                                    @Value("${CLOUDINARY_API_SECRET:}") String secret) {
        cloudinary = name.isBlank() || key.isBlank() || secret.isBlank() ? null :
                new Cloudinary(ObjectUtils.asMap("cloud_name", name, "api_key", key, "api_secret", secret, "secure", true));
    }

    public String uploadSchemeImage(org.springframework.web.multipart.MultipartFile file) throws Exception {
        if (cloudinary == null) throw new IllegalStateException("Cloudinary credentials are missing.");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Select an image to upload.");
        if (file.getSize() > 5L * 1024 * 1024) throw new IllegalArgumentException("Scheme image must be 5 MB or smaller.");
        String type = file.getContentType();
        if (type == null || !(type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/webp")))
            throw new IllegalArgumentException("Only JPEG, PNG and WebP images are supported.");
        byte[] bytes = file.getBytes();
        boolean valid = (bytes.length > 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255)
                || (bytes.length > 8 && bytes[0] == (byte)137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71)
                || (bytes.length > 12 && bytes[0] == 82 && bytes[1] == 73 && bytes[2] == 70 && bytes[3] == 70 && bytes[8] == 87 && bytes[9] == 69 && bytes[10] == 66 && bytes[11] == 80);
        if (!valid) throw new IllegalArgumentException("Invalid image file.");
        Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                "folder", "sagar/schemes", "resource_type", "image", "type", "upload"));
        Object url = result.get("secure_url");
        if (url == null) throw new IllegalStateException("Image upload did not return a URL.");
        return url.toString();
    }

    public void upload(Long applicationId, String filename, MultipartFile file, boolean compliance) throws Exception {
        if (cloudinary == null) throw new IllegalStateException("Cloudinary credentials are missing. Configure CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET.");
        if (file.getSize() > 10 * 1024 * 1024) throw new IllegalArgumentException("Maximum document size is 10 MB.");
        String publicId = publicId(applicationId, filename, compliance);
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", publicId, "resource_type", "raw", "type", "authenticated", "overwrite", true,
                "invalidate", true));
        if (result.get("public_id") == null) throw new IllegalStateException("Cloudinary upload did not return a public ID.");
    }

    public byte[] read(Long applicationId, String filename, boolean compliance) throws Exception {
        if (cloudinary != null) {
            // Cloudinary's time-limited download API is more reliable for authenticated raw assets
            // than a CDN delivery URL. Raw public IDs already contain the file extension.
            String url = cloudinary.privateDownload(publicId(applicationId, filename, compliance), null,
                    ObjectUtils.asMap("resource_type", "raw", "type", "authenticated",
                            "expires_at", java.time.Instant.now().getEpochSecond() + 120));
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(35)).GET().build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() == 200) return res.body();
            log.error("Cloudinary document retrieval failed: HTTP {}, applicationId={}, compliance={}, filename={}, cloudinaryError={}",
                    res.statusCode(), applicationId, compliance, filename,
                    res.headers().firstValue("X-Cld-Error").orElse("not provided"));
            // Do not silently hide Cloudinary failures as missing local files.
            // Legacy local files are checked below, but the Cloudinary error is now visible in logs.
        }
        Path root = Paths.get(System.getProperty("user.dir"), "uploads", "applications", applicationId.toString());
        if (compliance) root = root.resolve("compliance");
        root = root.toAbsolutePath().normalize();
        Path target = root.resolve(filename).normalize();
        if (!target.startsWith(root) || !Files.isRegularFile(target)) {
            log.warn("Document not found in local legacy storage: applicationId={}, compliance={}, filename={}",
                    applicationId, compliance, filename);
            throw new java.io.FileNotFoundException(filename);
        }
        return Files.readAllBytes(target);
    }

    private String publicId(Long id, String filename, boolean compliance) {
        if (id == null || filename == null || !filename.matches("[a-zA-Z0-9._-]+"))
            throw new IllegalArgumentException("Invalid document identifier.");
        return "sagar/applications/" + id + (compliance ? "/compliance/" : "/") + filename;
    }
}
