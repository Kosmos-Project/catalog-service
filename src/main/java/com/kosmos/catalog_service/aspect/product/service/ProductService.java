package com.kosmos.catalog_service.aspect.product.service;

import com.google.gson.Gson;
import com.kosmos.catalog_service.aspect.product.dao.Product;
import com.kosmos.catalog_service.aspect.product.dao.ProductRepository;
import com.kosmos.catalog_service.aspect.product.dto.*;
import com.kosmos.catalog_service.common.message.MessageConfig;
import com.kosmos.catalog_service.common.storage.FileMetadata;
import com.kosmos.catalog_service.common.storage.FileService;
import com.kosmos.catalog_service.common.storage.FileType;
import com.kosmos.catalog_service.common.storage.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final MessageSource msgSrc = MessageConfig.getProductMessageSource();
    private final ProductRepository productRepository;
    private final FileService fileServ;

    //FIXME: 아래 코드는 CRUD 구현 참고용 예시 코드입니다.

    // CREATE
    @Transactional
    public Long createProduct(CreateProductReqDto reqDto) throws Exception {
        // 받은 사용자 정보와 새 입력 정보로 새 게시물 정보 생성
        Product product = Product.builder()
                .contents(reqDto.getContents())
                .timestamp(LocalDateTime.now())
                .edited(false)
                .serializedHashTags(String.join(",", reqDto.getHashTags()))
                .disclosure(reqDto.getDisclosure())
                .geoTagLat(reqDto.getGeoTagLat().doubleValue())
                .geoTagLong(reqDto.getGeoTagLong().doubleValue())
                .build();

        // save
        productRepository.save(product);

        // 게시물 파일 저장소 생성
        fileServ.createProductFileStorage(product.getId());

        // 게시물 id 반환
        return product.getId();
    }

    // READ
    @Transactional(readOnly = true)
    public Page<Product> fetchProductByDefault(Integer pageIndex, Long topProductId) {
        // 기본 조건에 따른 최신 게시물 인출 (커뮤니티 메인화면 조회시)
        // 조건: 가장 최신의 전체 공개 게시물 또는 친구의 게시물 10개 조회
        // 추가조건: 만약 fromId(최초 로딩 시점)를 설정했다면 해당 시점 이전의 게시물만 검색
        if (pageIndex == null) {
            pageIndex = 0;
        }
        Pageable pageQuery = PageRequest.of(pageIndex, 10, Sort.Direction.DESC, "product_id");
        List<Long> friendAccountIdList = new ArrayList<>();


        if (topProductId != null) {
            return productRepository
                    .findAllByDefaultOptionAndTopProductId(topProductId, friendAccountIdList, 1L, pageQuery);
        } else {
            return productRepository
                    .findAllByDefaultOption(friendAccountIdList, 1L, pageQuery);
        }
    }

    @Transactional(readOnly = true)
    public Page<Product> fetchProductByPet(Long petId, Integer pageIndex) {
        // 태그된 펫으로 게시물 인출 (펫 피드 조회시)
        if (pageIndex == null) {
            pageIndex = 0;
        }
        Pageable pageQuery = PageRequest.of(pageIndex,10, Sort.Direction.DESC, "product_id");

        return productRepository.findAllByTaggedPetId(petId, pageQuery);
    }

    @Transactional(readOnly = true)
    public Product fetchProductById(Long productId) throws Exception {
        // 게시물 고유번호로 게시물 인출 (게시물 단일 불러오기시 사용)
        return productRepository.findById(productId)
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));
    }

    public byte[] fetchProductImage(Long productId, Integer fileIndex) throws Exception {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));

        // 이미지 파일 인출
        return fileServ.readFileFromFileMetadataListJson(currentProduct.getImageAttachments(), fileIndex, ImageUtil.GENERAL_IMAGE);
    }

    public ResponseEntity<byte[]> fetchProductVideo(String fileUrl, String range) throws Exception {
        Long fileSize = fileServ.getFileSize(fileUrl);
        String fileType = fileServ.getFileExtension(fileUrl);

        long rangeStart = 0;
        long rangeEnd;
        byte[] data;

        // HTTP Range 필드가 비어있으면 파일 전체 fetch
        if (range == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "video/" + fileType)
                    .header("Content-Length", String.valueOf(fileSize))
                    .body(fileServ.readByteRange(fileUrl, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
        }

        // 요청받은 Range 에 따라 파일을 나누어 fetch
        String[] ranges = range.split("-");
        rangeStart = Long.parseLong(ranges[0].substring(6));
        if (ranges.length > 1) {
            rangeEnd = Long.parseLong(ranges[1]);
        } else {
            rangeEnd = fileSize - 1;
        }
        if (fileSize < rangeEnd) {
            rangeEnd = fileSize - 1;
        }

        System.out.println("Video Streaming... | Range: bytes=" + rangeStart + "-" + rangeEnd);
        data = fileServ.readByteRange(fileUrl, rangeStart, rangeEnd);

        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", "video/" + fileType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", contentLength)
                .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
    }

    public byte[] fetchProductFile(Long productId, Integer fileIndex) throws Exception {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));

        // 일반 파일 인출
        return fileServ.readFileFromFileMetadataListJson(currentProduct.getFileAttachments(), fileIndex, ImageUtil.NOT_IMAGE);
    }

    // UPDATE
    @Transactional
    public void updateProduct(UpdateProductReqDto reqDto) throws Exception {
        // 받은 사용자 정보와 게시물 id로 게시물 정보 수정
        Product currentProduct = productRepository.findByAuthorAndId(1L, reqDto.getId())
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));

        if (!reqDto.getContents().equals(currentProduct.getContents())) {
            currentProduct.setContents(reqDto.getContents());
        }
        if (!String.join(",", reqDto.getHashTags()).equals(currentProduct.getSerializedHashTags())) {
            currentProduct.setSerializedHashTags(String.join(",", reqDto.getHashTags()));
        }
        if (!reqDto.getDisclosure().equals(currentProduct.getDisclosure())) {
            currentProduct.setDisclosure(reqDto.getDisclosure());
        }
        if (reqDto.getGeoTagLat().doubleValue() != currentProduct.getGeoTagLat()) {
            currentProduct.setGeoTagLat(reqDto.getGeoTagLat().doubleValue());
        }
        if (reqDto.getGeoTagLong().doubleValue() != currentProduct.getGeoTagLong()) {
            currentProduct.setGeoTagLong(reqDto.getGeoTagLong().doubleValue());
        }
        currentProduct.setEdited(true);

        // save
        productRepository.save(currentProduct);
    }

    @Transactional
    public List<FileMetadata> updateProductFile(UpdateProductFileReqDto reqDto, FileType fileType) throws Exception {
        // 기존 게시물 정보 로드
        Product currentProduct = productRepository.findByAuthorAndId(1L, reqDto.getId())
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));

        // 첨부파일 인출
        List<MultipartFile> uploadedFileList = reqDto.getFileList();

        List<FileMetadata> fileMetadataList;
        if (uploadedFileList.size() == 0) {
            throw new Exception(
                    msgSrc.getMessage("error.fileList.empty", null, Locale.ENGLISH)
            );
        }

        switch (fileType) {
            case GENERAL_FILE -> {
                // 해당 게시물의 파일 스토리지에 일반 파일 저장
                fileMetadataList = fileServ.saveProductFileAttachments(reqDto.getId(), uploadedFileList);

                // 파일정보 DB 데이터 업데이트
                currentProduct.setFileAttachments(new Gson().toJson(fileMetadataList));
                productRepository.save(currentProduct);
                return fileMetadataList;
            }
            case IMAGE_FILE -> {
                // 해당 게시물의 이미지 스토리지에 이미지 파일 저장
                fileMetadataList = fileServ.saveProductImageAttachments(reqDto.getId(), uploadedFileList);

                // 파일정보 DB 데이터 업데이트
                currentProduct.setImageAttachments(new Gson().toJson(fileMetadataList));
                productRepository.save(currentProduct);
                return fileMetadataList;
            }
            case VIDEO_FILE -> {
                // 해당 게시물의 비디오 스토리지에 비디오 파일 저장
                fileMetadataList = fileServ.saveProductVideoAttachments(reqDto.getId(), uploadedFileList);

                // 파일정보 DB 데이터 업데이트
                currentProduct.setVideoAttachments(new Gson().toJson(fileMetadataList));
                productRepository.save(currentProduct);
                return fileMetadataList;
            }
            default -> throw new Exception(
                    msgSrc.getMessage("error.product.invalidFileType", null, Locale.ENGLISH)
            );
        }
    }

    // DELETE
    @Transactional
    public void deleteProduct(DeleteProductReqDto reqDto) throws Exception {
        // 받은 사용자 정보와 게시물 id로 게시물 정보 삭제
        Product product = productRepository.findByAuthorAndId(1L, reqDto.getId())
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));
        fileServ.deleteProductFileStorage(product.getId());
        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductFile(DeleteProductFileReqDto reqDto, FileType fileType) throws Exception {
        // 기존 게시물 정보 로드
        Product currentProduct = productRepository.findByAuthorAndId(1L, reqDto.getId())
                .orElseThrow(() -> new Exception(
                        msgSrc.getMessage("error.product.notExists", null, Locale.ENGLISH)
                ));

        switch (fileType) {
            case GENERAL_FILE -> {
                // 기존 게시물의 모든 일반 파일 삭제
                fileServ.deleteProductFiles(currentProduct.getFileAttachments(), ImageUtil.NOT_IMAGE);

                // 기존 게시물의 fileAttachments 컬럼 null 설정 후 업데이트
                currentProduct.setFileAttachments(null);
            }
            case IMAGE_FILE -> {
                // 기존 게시물의 모든 이미지 파일 삭제
                fileServ.deleteProductFiles(currentProduct.getImageAttachments(), ImageUtil.GENERAL_IMAGE);

                // 기존 게시물의 imageAttachments 컬럼 null 설정 후 업데이트
                currentProduct.setImageAttachments(null);
            }
            case VIDEO_FILE -> {
                // 기존 게시물의 모든 비디오 파일 삭제
                fileServ.deleteProductFiles(currentProduct.getVideoAttachments(), ImageUtil.NOT_IMAGE);

                // 기존 게시물의 videoAttachments 컬럼 null 설정 후 업데이트
                currentProduct.setVideoAttachments(null);
            }
            default -> throw new Exception(
                    msgSrc.getMessage("error.product.invalidFileType", null, Locale.ENGLISH)
            );
        }
        productRepository.save(currentProduct);
    }
}
