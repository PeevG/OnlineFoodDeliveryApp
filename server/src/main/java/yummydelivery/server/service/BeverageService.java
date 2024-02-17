package yummydelivery.server.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.view.BeverageView;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.BeverageNotFoundException;
import yummydelivery.server.exceptions.InvalidProductTypeException;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.model.Product;
import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.utils.CommonUtils;

import java.util.List;

@Service
@Slf4j
public class BeverageService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final CommonUtils utils;

    public BeverageService(ProductRepository productRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService, CommonUtils utils) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
        this.utils = utils;
    }

    public BeverageView getBeverageById(Long id) {
        Product product = productRepository
                .findById(id).orElseThrow(() -> new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));
        BeverageEntity beverageEntity;
        if (product.getProductType().equals(ProductTypeEnum.BEVERAGE)) {
            beverageEntity = (BeverageEntity) product;
        } else {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "Unexpected product type: " + product.getProductType());
        }
        return modelMapper.map(beverageEntity, BeverageView.class);
    }

    public void addBeverage(BeverageDTO addBeverageDTO, MultipartFile productImage) {
        if (utils.productWithThisNameExist(addBeverageDTO.getName())) {
            throw new IllegalArgumentException("Cannot use this product name. it's already assigned");
        }
        BeverageEntity beverageEntity = modelMapper.map(addBeverageDTO, BeverageEntity.class);

        if (productImage == null || productImage.isEmpty()) {
            String defaultProductImageURL = "https://res.cloudinary.com/dncjjyvqi/image/upload/v1707229014/YummyDeliveryImages/defaultProductImage.png";
            beverageEntity.setImageURL(defaultProductImageURL);
            log.info("Product image is not provided. Default one is used");
        } else {
            cloudinaryService.validateImageFile(productImage);
            String imageUrl = cloudinaryService.uploadImage(productImage, addBeverageDTO.getName());
            beverageEntity.setImageURL(imageUrl);
            log.info("Product image is provided and uploaded successfully to Cloudinary");
        }
        beverageEntity.setProductType(ProductTypeEnum.BEVERAGE);
        productRepository.save(beverageEntity);
    }


    public void updateBeverage(Long id, BeverageDTO dto, MultipartFile imageURL) {
        if (utils.productWithThisNameExist(dto.getName())) {
            throw new IllegalArgumentException("Cannot use this product name. it's already assigned");
        }
        Product product = getProductByIdOrElseThrow(id);
        if (!product.getProductType().equals(ProductTypeEnum.BEVERAGE)) {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "To update beverage, product type must be 'Beverage'");
        }

        BeverageEntity beverageEntity;
        beverageEntity = (BeverageEntity) product;

        if (imageURL == null || imageURL.isEmpty()) {
            updateBeverage(beverageEntity, dto);
        } else {
            cloudinaryService.validateImageFile(imageURL);
            String newImageURL = cloudinaryService.uploadImage(imageURL, dto.getName());
            updateBeverage(beverageEntity, dto, newImageURL);
            log.info("Product image is provided and uploaded successfully to Cloudinary");
        }
        productRepository.save(beverageEntity);
    }

    public Page<BeverageView> getAllBeverages(int page) {
        Page<BeverageEntity> beveragesPageable = productRepository.findAllBeveragesPageable(PageRequest.of(page, 6));

        List<BeverageView> viewList = beveragesPageable
                .getContent()
                .stream()
                .map(beverageEntity -> modelMapper.map(beverageEntity, BeverageView.class))
                .toList();

        return new PageImpl<>(viewList, beveragesPageable.getPageable(), beveragesPageable.getTotalElements());
    }

    private Product getProductByIdOrElseThrow(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(
                        () -> new BeverageNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found")
                );
    }

    private void updateBeverage(BeverageEntity beverageEntity, BeverageDTO dto) {
        beverageEntity.setName(dto.getName());
        beverageEntity.setPrice(dto.getPrice());
        beverageEntity.setMilliliters(dto.getMilliliters());
    }

    private void updateBeverage(BeverageEntity beverageEntity, BeverageDTO dto, String newImageURL) {
        beverageEntity.setName(dto.getName());
        beverageEntity.setPrice(dto.getPrice());
        beverageEntity.setMilliliters(dto.getMilliliters());
        beverageEntity.setImageURL(newImageURL);
    }


}
