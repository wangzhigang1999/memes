package com.memes.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.memes.model.common.FileUploadResult;
import com.memes.service.StorageService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service("ali-oss")
@ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "aliyun")
public class AliyunStorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(AliyunStorageServiceImpl.class);

    // --- Configuration Properties ---
    @Value("${storage.aliyun.endpoint}")
    private String endpoint;

    @Value("${storage.aliyun.access-key-id}")
    private String accessKeyId;

    @Value("${storage.aliyun.access-key-secret}")
    private String accessKeySecret;

    @Value("${storage.aliyun.bucket-name}")
    private String bucketName;

    @Value("${storage.aliyun.region}")
    private String region;

    @Value("${storage.aliyun.base-url}")
    private String baseUrl;

    private OSS ossClient;

    // --- Initialization and Cleanup ---

    @PostConstruct
    public void initialize() {
        log.info("Initializing Aliyun OSS Client...");
        log.debug("Endpoint: {}, Region: {}, Bucket: {}", endpoint, region, bucketName);

        if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(accessKeyId) || !StringUtils.hasText(accessKeySecret)
                || !StringUtils.hasText(bucketName) || !StringUtils.hasText(region) || !StringUtils.hasText(baseUrl)) {
            log.error("Aliyun OSS configuration is incomplete. Please check properties like "
                    + "storage.aliyun.endpoint, access-key-id, access-key-secret, bucket-name, region, base-url");
            return;
        }

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        this.ossClient = OSSClientBuilder.create().endpoint(endpoint)
                .credentialsProvider(new DefaultCredentialProvider(accessKeyId, accessKeySecret, ""))
                .clientConfiguration(clientBuilderConfiguration)
                .region(region).build();
        log.info("Aliyun OSS Client initialized successfully.");
    }

    @PreDestroy
    public void destroy() {
        if (this.ossClient != null) {
            log.info("Shutting down Aliyun OSS Client...");
            this.ossClient.shutdown();
            log.info("Aliyun OSS Client shut down.");
        }
    }

    // --- StorageService Implementation ---

    @Override
    public FileUploadResult store(byte[] bytes, String mime) {
        // Delegate to the more specific store method with an empty path
        return store(bytes, mime, "");
    }

    /**
     * Stores the byte array in OSS.
     *
     * @param bytes The file content.
     * @param mime The MIME type of the file.
     * @param path The desired directory path within the bucket (can be empty).
     * @return A FileUploadResult record containing the URL, object key (as fileName), and MIME type on success, or null if an error occurred during
     *         upload.
     */
    @Override
    public FileUploadResult store(byte[] bytes, String mime, String path) {
        if (ossClient == null) {
            log.error("OSS Client not initialized. Cannot store file.");
            return null; // Indicate failure
        }
        if (bytes == null || bytes.length == 0) {
            log.warn("Attempted to store empty byte array.");
            return null; // Indicate failure
        }
        if (!StringUtils.hasText(mime)) {
            log.warn("MIME type is missing, upload may proceed but Content-Type won't be set correctly.");
        }

        String fileExtension = MimeTypeUtils.getExtension(mime);
        String uniqueFileName = UUID.randomUUID() + fileExtension;
        // The objectName is the full path/key within the OSS bucket
        String objectName = buildObjectName(path, uniqueFileName);

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            // Set Content-Type only if mime is provided
            if (StringUtils.hasText(mime)) {
                metadata.setContentType(mime);
            }
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);

            log.debug("Uploading object '{}' to bucket '{}'", objectName, bucketName);
            ossClient.putObject(putObjectRequest);
            log.info("Successfully uploaded object '{}' to bucket '{}'", objectName, bucketName);

            String fileUrl = buildFileUrl(objectName);

            // Create and return the FileUploadResult record on success
            // Use objectName as the 'fileName' parameter for the record
            // Pass the original mime type as the 'type' parameter
            return new FileUploadResult(fileUrl, objectName, mime != null ? mime : "application/octet-stream"); // Provide default if mime was null

        } catch (OSSException oe) {
            log.error("OSS Exception during upload for object '{}'. Error Message: {}, Error Code: {}, Request ID: {}, Host ID: {}", objectName,
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId(), oe);
            return null; // Indicate failure
        } catch (ClientException ce) {
            log.error("OSS Client Exception during upload for object '{}'. Error Message: {}", objectName, ce.getMessage(), ce);
            return null; // Indicate failure
        } catch (IllegalArgumentException iae) {
            // Catch potential exception from FileUploadResult constructor if any input is null
            log.error("Failed to create FileUploadResult for object '{}'. Likely null input. Error: {}", objectName, iae.getMessage(), iae);
            return null; // Indicate failure
        } catch (Exception e) {
            log.error("Unexpected exception during upload for object '{}'", objectName, e);
            return null; // Indicate failure
        }
    }

    @Override
    public Map<String, Boolean> delete(String[] keyList) {
        if (ossClient == null) {
            log.error("OSS Client not initialized. Cannot delete files.");
            Map<String, Boolean> resultMap = new HashMap<>();
            for (String key : keyList) {
                if (key == null) {
                    throw new IllegalArgumentException("Key list contains null values");
                }
                resultMap.put(key, false);
            }
            return Map.copyOf(resultMap);
        }
        if (keyList == null || keyList.length == 0) {
            log.warn("Delete request received with empty key list.");
            return Map.of();
        }

        Map<String, Boolean> results = new HashMap<>();
        List<String> keysToDelete = java.util.Arrays.asList(keyList);

        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keysToDelete).withQuiet(false);

            log.debug("Deleting objects from bucket '{}': {}", bucketName, keysToDelete);
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            log.info("Successfully deleted objects: {}", deletedObjects);

            List<String> successfullyDeletedKeys = deletedObjects.stream().map(Object::toString).toList();
            for (String key : keyList) {
                results.put(key, successfullyDeletedKeys.contains(key));
            }

        } catch (OSSException oe) {
            log.error("OSS Exception during delete operation. Error Message: {}, Error Code: {}, Request ID: {}, Host ID: {}", oe.getErrorMessage(),
                    oe.getErrorCode(), oe.getRequestId(), oe.getHostId(), oe);
            for (String key : keyList) {
                results.putIfAbsent(key, false);
            }
        } catch (ClientException ce) {
            log.error("OSS Client Exception during delete operation. Error Message: {}", ce.getMessage(), ce);
            for (String key : keyList) {
                results.putIfAbsent(key, false);
            }
        } catch (Exception e) {
            log.error("Unexpected exception during delete operation", e);
            for (String key : keyList) {
                results.putIfAbsent(key, false);
            }
        }

        return results;
    }

    // --- Helper Methods ---

    private String buildObjectName(String path, String fileName) {
        String cleanedPath = path == null ? "" : path.trim();
        if (cleanedPath.startsWith("/")) {
            cleanedPath = cleanedPath.substring(1);
        }
        if (cleanedPath.endsWith("/")) {
            cleanedPath = cleanedPath.substring(0, cleanedPath.length() - 1);
        }

        if (cleanedPath.isEmpty()) {
            return fileName;
        } else {
            return "%s/%s".formatted(cleanedPath, fileName);
        }
    }

    private String buildFileUrl(String objectName) {
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanObjectName = objectName.startsWith("/") ? objectName.substring(1) : objectName;
        return "%s/%s".formatted(cleanBaseUrl, cleanObjectName);
    }

    private static class MimeTypeUtils {
        private static final Map<String, String> MIME_EXTENSION_MAP = Map.ofEntries(Map.entry("image/jpeg", ".jpg"), Map.entry("image/png", ".png"),
                Map.entry("image/gif", ".gif"), Map.entry("image/webp", ".webp"), Map.entry("image/svg+xml", ".svg"), Map.entry("video/mp4", ".mp4"),
                Map.entry("video/webm", ".webm"), Map.entry("application/pdf", ".pdf"), Map.entry("text/plain", ".txt"),
                Map.entry("text/html", ".html"), Map.entry("application/json", ".json"));

        public static String getExtension(String mimeType) {
            if (!StringUtils.hasText(mimeType)) {
                return "";
            }
            return MIME_EXTENSION_MAP.getOrDefault(mimeType.toLowerCase(), "");
        }
    }
}
