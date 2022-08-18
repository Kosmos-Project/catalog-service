package com.kosmos.catalog_service.aspect.product.api;

import com.kosmos.catalog_service.aspect.product.service.ProductService;
import com.kosmos.catalog_service.aspect.product.dao.Product;
import com.kosmos.catalog_service.aspect.product.dto.*;
import com.kosmos.catalog_service.common.http.DtoMetadata;
import com.kosmos.catalog_service.common.message.MessageConfig;
import com.kosmos.catalog_service.common.storage.FileMetadata;
import com.kosmos.catalog_service.common.storage.FileType;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RequiredArgsConstructor
@RestController
public class ProductController {
    private static final Logger logger = LogManager.getLogger();
    private final MessageSource msgSrc = MessageConfig.getProductMessageSource();
    private final ProductService productServ;

    // CREATE
    @PostMapping("/api/product/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductReqDto reqDto) {
        DtoMetadata dtoMetadata;
        Long productId;

        try {
            productId = productServ.createProduct(reqDto);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new CreateProductResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.product.create.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new CreateProductResDto(dtoMetadata, productId));
    }

    //READ
    @PostMapping("/api/product/fetch")
    public ResponseEntity<?> fetchProduct(@Valid @RequestBody FetchProductReqDto reqDto) {
        DtoMetadata dtoMetadata;
        final List<Product> productList;
        Pageable pageable = null;
        Boolean isLast = null;

        try {
            if (reqDto.getId() != null) {
                // 개별 게시물 조회 요청
                productList = new ArrayList<>();
                productList.add(productServ.fetchProductById(reqDto.getId()));
            } else if (reqDto.getPetId() != null) {
                // 펫 피드 조회 요청
                final Page<Product> productPage = productServ.fetchProductByPet(reqDto.getPetId(), reqDto.getPageIndex());
                productList = productPage.getContent();
                pageable = productPage.getPageable();
                isLast = productPage.isLast();
            } else {
                // 전체 게시물 조회 요청
                final Page<Product> productPage = productServ.fetchProductByDefault(reqDto.getPageIndex(), reqDto.getTopProductId());
                productList = productPage.getContent();
                pageable = productPage.getPageable();
                isLast = productPage.isLast();
            }
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new FetchProductResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.product.fetch.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new FetchProductResDto(dtoMetadata, productList, pageable, isLast));
    }

    @PostMapping("/api/product/image/fetch")
    public ResponseEntity<?> fetchProductImage(@Valid @RequestBody FetchProductImageReqDto reqDto) {
        DtoMetadata dtoMetadata;
        byte[] fileBinData;
        try {
            fileBinData = productServ.fetchProductImage(reqDto.getId(), reqDto.getIndex());
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new FetchProductImageResDto(dtoMetadata));
        }
        return ResponseEntity.ok(fileBinData);
    }

    @GetMapping("/api/product/video/fetch")
    public ResponseEntity<?> fetchProductVideo(@RequestParam(name = "url") String fileUrl, @RequestHeader(value = "Range", required = false) String httpRangeList) {
        DtoMetadata dtoMetadata;
        ResponseEntity<?> responseEntity;

        try {
            responseEntity = productServ.fetchProductVideo(URLDecoder.decode(fileUrl, StandardCharsets.UTF_8), httpRangeList);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new FetchProductVideoResDto(dtoMetadata));
        }

        return responseEntity;
    }

    @PostMapping("/api/product/file/fetch")
    public ResponseEntity<?> fetchProductFile(@Valid @RequestBody FetchProductFileReqDto reqDto) {
        DtoMetadata dtoMetadata;
        byte[] fileBinData;
        try {
            fileBinData = productServ.fetchProductFile(reqDto.getId(), reqDto.getIndex());
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new FetchProductFileResDto(dtoMetadata));
        }
        return ResponseEntity.ok(fileBinData);
    }

    // UPDATE
    @PostMapping("/api/product/update")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody UpdateProductReqDto reqDto) {
        DtoMetadata dtoMetadata;
        try {
            productServ.updateProduct(reqDto);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new UpdateProductResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.product.update.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new UpdateProductResDto(dtoMetadata));
    }

    @PostMapping("/api/product/file/update")
    public ResponseEntity<?> updateProductFile(@ModelAttribute UpdateProductFileReqDto reqDto) {
        DtoMetadata dtoMetadata;
        List<FileMetadata> fileMetadataList;
        try {
            fileMetadataList = productServ.updateProductFile(reqDto, FileType.valueOf(reqDto.getFileType().replace("\"", "")));
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new UpdateProductFileResDto(dtoMetadata, null));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.productFile.update.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new UpdateProductFileResDto(dtoMetadata, fileMetadataList));
    }

    @PostMapping("/api/product/media/update")
    @Deprecated
    public ResponseEntity<?> updateProductMedia(@ModelAttribute UpdateProductFileReqDto reqDto) {
        DtoMetadata dtoMetadata;
        List<FileMetadata> fileMetadataList;
        try {
            fileMetadataList = productServ.updateProductFile(reqDto, FileType.IMAGE_FILE);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new UpdateProductFileResDto(dtoMetadata, null));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.productMedia.update.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new UpdateProductFileResDto(dtoMetadata, fileMetadataList));
    }

    // DELETE
    @PostMapping("/api/product/delete")
    public ResponseEntity<?> deleteProduct(@Valid @RequestBody DeleteProductReqDto reqDto) {
        DtoMetadata dtoMetadata;
        try {
            productServ.deleteProduct(reqDto);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new DeleteProductResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.product.delete.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new DeleteProductResDto(dtoMetadata));
    }

    @PostMapping("/api/product/file/delete")
    public ResponseEntity<?> deleteProductFile(@Valid @RequestBody DeleteProductFileReqDto reqDto) {
        DtoMetadata dtoMetadata;
        try {
            productServ.deleteProductFile(reqDto, FileType.valueOf(reqDto.getFileType().replace("\"", "")));
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new DeleteProductFileResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.productFile.delete.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new DeleteProductFileResDto(dtoMetadata));
    }

    @PostMapping("/api/product/media/delete")
    @Deprecated
    public ResponseEntity<?> deleteProductMedia(@Valid @RequestBody DeleteProductFileReqDto reqDto) {
        DtoMetadata dtoMetadata;
        try {
            productServ.deleteProductFile(reqDto, FileType.IMAGE_FILE);
        } catch (Exception e) {
            logger.warn(e.toString());
            dtoMetadata = new DtoMetadata(e.getMessage(), e.getClass().getName());
            return ResponseEntity.status(400).body(new DeleteProductFileResDto(dtoMetadata));
        }
        dtoMetadata = new DtoMetadata(msgSrc.getMessage("res.productFile.delete.success", null, Locale.ENGLISH));
        return ResponseEntity.ok(new DeleteProductFileResDto(dtoMetadata));
    }
}
