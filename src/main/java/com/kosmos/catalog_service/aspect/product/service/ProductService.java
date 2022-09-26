package com.kosmos.catalog_service.aspect.product.service;

import com.kosmos.catalog_service.aspect.product.dao.entity.Product;
import com.kosmos.catalog_service.aspect.product.dao.repository.ProductRepository;
import com.kosmos.catalog_service.common.message.MessageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final MessageSource msgSrc = MessageConfig.getProductMessageSource();
    private final ProductRepository productRepository;

    @Transactional
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

/*
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
*/

}
