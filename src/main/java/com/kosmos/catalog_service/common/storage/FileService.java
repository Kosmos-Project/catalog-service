package com.kosmos.catalog_service.common.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosmos.catalog_service.common.message.MessageConfig;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@NoArgsConstructor
@Service
public class FileService {
    private final MessageSource msgSrc = MessageConfig.getStorageMessageSource();
    /***************************** 변경 금지 구역 *******************************/
    // IMPORTANT: storageRootPath 는 환경 변수별로 분리된 application.properties 에 기록합니다. (storage.rootPath 값)
    @Value("${storage.rootPath}")
    private String storageRootPath;
    /**************************************************************************/

    // 파일 메타데이터 목록(stringify 된 JSON)을 이용하여 파일 읽기
    public byte[] readFileFromFileMetadataListJson(String fileMetadataListJson, Integer fileIndex, int operationCode) throws IOException {
        Type collectionType = new TypeToken<List<FileMetadata>>(){}.getType();
        List<FileMetadata> fileMetadataList = new Gson()
                .fromJson(fileMetadataListJson, collectionType);
        String fileUrl;
        if(operationCode == ImageUtil.NOT_IMAGE) {
            fileUrl = fileMetadataList.get(fileIndex).getUrl();
        }
        else {
            fileUrl = ImageUtil.createImageUrl(fileMetadataList.get(fileIndex).getUrl(), operationCode);
        }

        InputStream mediaStream = new FileInputStream(fileUrl);
        byte[] fileBinData = IOUtils.toByteArray(mediaStream);
        mediaStream.close();
        return fileBinData;
    }

    // 특정 게시물 데이터 폴더 경로 조회
    public Path getPostFileStoragePath(Long productId) {
        return Paths.get(storageRootPath, "catalog", "products", "product_" + productId);
    }
    // 특정 게시물 댓글 데이터 폴더 경로 조회 - TODO: 장래 댓글 첨부파일 기능 구현시 같이 구현예정
    // TODO: getCommentFileStoragePath(Long commentId) 구현

    // 게시물 데이터 폴더 생성
    public void createPostFileStorage(Long productId) throws Exception {
        Path postAttachedFileStorage = getPostFileStoragePath(productId);
        Files.createDirectories(postAttachedFileStorage);
        FileUtil.makeNewChildDir(postAttachedFileStorage.toFile(), "media");
        FileUtil.makeNewChildDir(postAttachedFileStorage.toFile(), "general");
        FileUtil.makeNewChildDir(postAttachedFileStorage.toFile(), "comments");
    }
    // 게시물 데이터 폴더 삭제
    public void deletePostFileStorage(Long productId) throws Exception {
        Path postAttachedFileStorage = getPostFileStoragePath(productId);
        FileUtils.deleteDirectory(postAttachedFileStorage.toFile());
    }

    // 데이터 파일 삭제
    public void deleteFile(String filePath) throws IOException {
        FileUtils.delete(new File(filePath));
    }

    // 이미지 데이터 파일 삭제
    public void deleteImageFile(String filePath) throws IOException {
        String defaultFilePath = filePath.split("\\.")[0];
        String fileFormat = filePath.split("\\.")[1];

        String originalFilePath = defaultFilePath + "original." + fileFormat;
        String generalFilePath = defaultFilePath + "general." + fileFormat;
        String thumbnailFilePath = defaultFilePath + "thumbnail." + fileFormat;

        deleteFile(originalFilePath);
        deleteFile(generalFilePath);
        deleteFile(thumbnailFilePath);
    }

    // 게시물 데이터 리스트 전체 삭제 (임시)
    public void deletePostFiles(String fileMetadataListJson, int operationCode) throws RuntimeException {
        Type collectionType = new TypeToken<List<FileMetadata>>(){}.getType();
        List<FileMetadata> fileMetadataList = new Gson()
                .fromJson(fileMetadataListJson, collectionType);

        if(operationCode == ImageUtil.NOT_IMAGE) {
            fileMetadataList.forEach(fileMetadata -> {
                try {
                    deleteFile(fileMetadata.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else {
            fileMetadataList.forEach(fileMetadata -> {
                try {
                    deleteImageFile(fileMetadata.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    
    // 게시물 이미지 파일 저장
    public List<FileMetadata> savePostImageAttachments(Long productId, List<MultipartFile> uploadedFiles) throws Exception {
        // 업로드 다중파일 저장 경로
        Path savePath = getPostFileStoragePath(productId).resolve("media");
        // 업로드 가능한 확장자
        String[] acceptableExtensions = new String[]{
                "jpg","png","jpeg", "gif", "webp"
        };
        // 업로드 개별 파일 용량 제한 (100MB)
        long fileSizeLimit = 100000000;
        // 업로드 파일 갯수 확인
        this.checkFileCount(uploadedFiles, 20);

        // 파일 메타데이터 리스트
        List<FileMetadata> fileMetaDataList = new ArrayList<>();

        // 해당 게시물 데이터 디렉토리 초기화
        FileUtils.cleanDirectory(savePath.toFile());

        for (MultipartFile uploadedFile : uploadedFiles) {
            try {
                // 업로드 된 파일 확장자
                String fileFormat = FilenameUtils.getExtension(Objects.requireNonNull(uploadedFile.getOriginalFilename()));
                // 파일 유효성 검사
                checkFileValidity(savePath, uploadedFile, acceptableExtensions, fileSizeLimit);

                // 업로드 파일 저장 파일명 설정
                String fileName = ("post_" + productId + "_" + uploadedFile.getOriginalFilename()).split("\\.")[0] + "_";

                // 이미지 파일 최적화 및 여러 버전으로 저장
                ImageUtil.optimizeAndSaveImage(fileName, uploadedFile, savePath);

                // 파일 메타데이터 정보 생성
                FileMetadata fileMetaData = new FileMetadata(
                        fileName,
                        uploadedFile.getSize(),
                        "post", FileType.IMAGE_FILE.getValue(),
                        savePath.resolve(fileName) + "." + fileFormat
                );

                fileMetaDataList.add(fileMetaData);
            } catch (Exception e) {
                // 업로드 실패시 해당 게시물 데이터 디렉토리 초기화
                FileUtils.cleanDirectory(savePath.toFile());
                throw e;
            }
        }

        return fileMetaDataList;
    }
    
    //TODO: video, audio 가공 및 업로드 로직 만들기

    // 게시물 일반 첨부파일 저장
    public List<FileMetadata> savePostFileAttachments(Long productId, List<MultipartFile> uploadedFiles) throws Exception {
        // 업로드 다중파일 저장 경로
        Path savePath = getPostFileStoragePath(productId).resolve("general");
        // 업로드 가능한 확장자
        String[] acceptableExtensions = new String[]{
                "doc", "docx", "hwp", "pdf", "txt", "ppt", "pptx", "psd", "ai", "xls", "xlsx",
                "rar", "tar", "zip", "exe", "apk"
        };
        // 업로드 개별 파일 용량 제한 (100MB)
        long fileSizeLimit = 100000000;
        // 업로드 파일 갯수 확인
        this.checkFileCount(uploadedFiles, 10);

        // 파일 메타데이터 리스트
        List<FileMetadata> fileMetaDataList = new ArrayList<>();

        // 해당 게시물 데이터 디렉토리 초기화
        FileUtils.cleanDirectory(savePath.toFile());

        for (MultipartFile uploadedFile : uploadedFiles) {
            try {
                // 파일 유효성 검사
                checkFileValidity(savePath, uploadedFile, acceptableExtensions, fileSizeLimit);

                // 업로드 파일 저장 파일명 설정
                String fileName = "post_" + productId + "_" + uploadedFile.getOriginalFilename();

                // 파일 저장
                uploadedFile.transferTo(savePath.resolve(fileName));
                // 파일 메타데이터 정보 생성
                FileMetadata fileMetaData = new FileMetadata(
                        fileName,
                        uploadedFile.getSize(),
                        "post", FileType.GENERAL_FILE.getValue(),
                        savePath.resolve(fileName).toString()
                );

                fileMetaDataList.add(fileMetaData);
            } catch (Exception e) {
                // 업로드 실패시 해당 게시물 데이터 디렉토리 초기화
                FileUtils.cleanDirectory(savePath.toFile());
                throw e;
            }
        }

        return fileMetaDataList;
    }

    // 파일 검증 로직
    public void checkFileValidity(Path savePath, MultipartFile uploadedFile, String[] acceptableExtensions, Long fileSizeLimit) throws Exception {
        // 업로드 파일 원본 파일명
        String originalFileName = uploadedFile.getOriginalFilename();

        // 저장할 데이터 디렉토리 존재 여부 검사
        if (!savePath.toFile().exists()) {
            throw new FileNotFoundException(msgSrc.getMessage("error.dir.notExist", null, Locale.ENGLISH));
        }
        // 빈 파일 검사
        if (uploadedFile.isEmpty()) {
            throw new Exception(msgSrc.getMessage("error.file.empty", new String[]{originalFileName}, Locale.ENGLISH));
        }
        // 파일 확장자 적합성 검사
        else if (Arrays.stream(acceptableExtensions).noneMatch(
                extension -> FilenameUtils.getExtension(Objects.requireNonNull(originalFileName)).toLowerCase().equals(extension)
        )) {
            throw new Exception(msgSrc.getMessage("error.file.extension.valid", new String[]{originalFileName}, Locale.ENGLISH));
        }
        // 파일 크기 적합성 검사
        else if (uploadedFile.getSize() > fileSizeLimit) {
            throw new Exception(msgSrc.getMessage("error.file.size", new String[]{originalFileName}, Locale.ENGLISH));
        }
    }
    
    // 업로드 파일 갯수 검증 로직
    private void checkFileCount(List<MultipartFile> uploadedFiles, Integer fileCountLimit) throws Exception {
        if (uploadedFiles.size() > fileCountLimit) {
            throw new Exception(msgSrc.getMessage("error.file.count", null, Locale.ENGLISH));
        }
    }
}
