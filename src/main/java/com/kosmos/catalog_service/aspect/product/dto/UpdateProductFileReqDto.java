package com.kosmos.catalog_service.aspect.product.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateProductFileReqDto {
    @PositiveOrZero(message = "valid.product.id.notNegative")
    Long id;
    @Size(max = 10, message = "valid.product.file.count")
    List<MultipartFile> fileList;
    String fileType;
}
