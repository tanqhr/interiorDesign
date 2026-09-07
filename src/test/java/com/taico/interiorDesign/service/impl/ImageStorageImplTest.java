package com.taico.interiorDesign.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private ImageStorageServiceImpl imageStorageService;

    @BeforeEach
    void setUp() {
        imageStorageService =
                new ImageStorageServiceImpl(tempDir.toString());
    }

    @Test
    void store_shouldStoreImageSuccessfully() throws Exception {

        // Arrange
        byte[] content = "test image content".getBytes();

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // Act
        String result = imageStorageService.store(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("uploads/"));
        assertTrue(result.endsWith("_test.jpg"));

        String storedFileName =
                result.substring("uploads/".length());

        Path storedFile =
                tempDir.resolve(storedFileName);

        assertTrue(Files.exists(storedFile));

        assertArrayEquals(
                content,
                Files.readAllBytes(storedFile)
        );
    }

    @Test
    void store_shouldThrowException_whenFileIsEmpty() {

        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.store(file)
                );

        assertEquals(
                "Файлът е празен.",
                exception.getMessage()
        );
    }

    @Test
    void store_shouldThrowException_whenFileIsNotImage() {

        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "some text".getBytes()
        );

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.store(file)
                );

        assertEquals(
                "Файлът трябва да бъде изображение.",
                exception.getMessage()
        );
    }

    @Test
    void store_shouldThrowException_whenContentTypeIsNull() {

        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                null,
                "image content".getBytes()
        );

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.store(file)
                );

        assertEquals(
                "Файлът трябва да бъде изображение.",
                exception.getMessage()
        );
    }

    @Test
    void store_shouldThrowRuntimeException_whenIOExceptionOccurs()
            throws Exception {

        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        /*
         * Създаваме файл със същото име, което ще бъде генерирано,
         * не е удобно заради UUID.
         *
         * Вместо това използваме MultipartFile,
         * чийто InputStream хвърля IOException.
         */

        MultipartFile brokenFile = new MultipartFile() {

            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return "test.jpg";
            }

            @Override
            public String getContentType() {
                return "image/jpeg";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 100;
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public java.io.InputStream getInputStream()
                    throws IOException {

                throw new IOException("Test IOException");
            }

            @Override
            public org.springframework.core.io.Resource getResource() {
                return null;
            }

            @Override
            public void transferTo(
                    java.io.File dest) {
            }

            @Override
            public void transferTo(
                    Path dest) {
            }
        };

        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> imageStorageService.store(brokenFile)
                );

        assertEquals(
                "Грешка при записването на изображението.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());
        assertInstanceOf(
                IOException.class,
                exception.getCause()
        );
    }
}