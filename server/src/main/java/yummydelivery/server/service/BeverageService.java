package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.BeverageDTO.AddOrUpdateBeverageDTO;
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.view.BeverageView;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.BeverageNotFoundException;
import yummydelivery.server.exceptions.InvalidProductTypeException;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.BeverageEntity;


import yummydelivery.server.model.Product;

import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.List;

@Service
public class BeverageService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;

    public BeverageService(ProductRepository productRepository, ModelMapper modelMapper, AuthenticationFacade authenticationFacade) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.authenticationFacade = authenticationFacade;
    }

    public BeverageDTO getBeverage(Long id) {
        Product product = productRepository
                .findById(id).orElseThrow(() -> new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));
        BeverageEntity beverageEntity;
        if (product.getProductType().equals(ProductTypeEnum.BEVERAGE)) {
            beverageEntity = (BeverageEntity) product;
        } else {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "Unexpected product type: " + product.getProductType());
        }
        return modelMapper.map(beverageEntity, BeverageDTO.class);
    }

    public void addBeverage(AddOrUpdateBeverageDTO addBeverageDTO) {

        authenticationFacade.checkIfUserIsAdmin();

        BeverageEntity beverageEntity = modelMapper.map(addBeverageDTO, BeverageEntity.class);
        beverageEntity.setProductType(ProductTypeEnum.BEVERAGE);
        productRepository.save(beverageEntity);
    }

    public void updateBeverage(Long id, AddOrUpdateBeverageDTO dto) {
        authenticationFacade.checkIfUserIsAdmin();
        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BeverageNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found")
                        );
        if (!product.getProductType().equals(ProductTypeEnum.BEVERAGE)) {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "To update beverage, product type must be 'Beverage'");
        }
        BeverageEntity beverageEntity;
        beverageEntity = (BeverageEntity) product;
        beverageEntity.setPrice(dto.getPrice());
        beverageEntity.setName(dto.getName());
        beverageEntity.setMilliliters(dto.getMilliliters());
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
}
