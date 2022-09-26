package com.kosmos.catalog_service.aspect.product.api;

import com.kosmos.catalog_service.aspect.product.dao.entity.Product;
import com.kosmos.catalog_service.aspect.product.dto.product.ResponseProductDto;
import com.kosmos.catalog_service.aspect.product.service.ProductService;
import com.kosmos.catalog_service.common.message.MessageConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/catalog")
public class ProductController {
    private static final Logger logger = LogManager.getLogger();
    private final MessageSource msgSrc = MessageConfig.getProductMessageSource();
    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<ResponseProductDto>> getProducts() {
        Iterable<Product> orderList = productService.getAllProducts();

        List<ResponseProductDto> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseProductDto.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /*// CREATE
    @PostMapping("/create")
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
    @PostMapping("/fetch")
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

    // UPDATE
    @PostMapping("/update")
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

    // DELETE
    @PostMapping("/delete")
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
*/
}
