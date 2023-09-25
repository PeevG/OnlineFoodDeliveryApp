package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.BeverageDTO.AddOrUpdateBeverageDTO;
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.exceptions.BeverageNotFoundException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.repository.BeverageRepository;
import yummydelivery.server.security.AuthenticationFacade;

@Service
public class BeverageService {
    private final BeverageRepository beverageRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;

    public BeverageService(BeverageRepository beverageRepository, ModelMapper modelMapper, AuthenticationFacade authenticationFacade) {
        this.beverageRepository = beverageRepository;
        this.modelMapper = modelMapper;
        this.authenticationFacade = authenticationFacade;
    }

    public BeverageDTO getBeverage(Long id) {
        BeverageEntity beverageEntity = beverageRepository
                .findById(id).orElseThrow(
                        () -> new BeverageNotFoundException(HttpStatus.NOT_FOUND, "Beverage with id " + id + " not found"));
        return modelMapper.map(beverageEntity, BeverageDTO.class);
    }

    public void addBeverage(AddOrUpdateBeverageDTO addBeverageDTO) {

        authenticationFacade.checkIfUserIsAuthorized();

        BeverageEntity beverageEntity = BeverageEntity
                .builder()
                .name(addBeverageDTO.getName())
                .milliliters(addBeverageDTO.getMilliliters())
                .price(addBeverageDTO.getPrice())
                .build();
        beverageRepository.save(beverageEntity);
    }

    public void updateBeverage(Long id, AddOrUpdateBeverageDTO dto) {
        authenticationFacade.checkIfUserIsAuthorized();

        BeverageEntity beverageEntity =
                beverageRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BeverageNotFoundException(HttpStatus.NOT_FOUND, "Beverage with id " + id + " not found")
                        );

        beverageEntity.setMilliliters(dto.getMilliliters());
        beverageEntity.setName(dto.getName());
        beverageEntity.setPrice(dto.getPrice());
        beverageRepository.save(beverageEntity);
    }

    public void deleteBeverage(Long id) {

        authenticationFacade.checkIfUserIsAuthorized();

        BeverageEntity beverageEntity = beverageRepository
                .findById(id)
                .orElseThrow(
                        () -> new BeverageNotFoundException(HttpStatus.NOT_FOUND, "Beverage with id " + id + " not found")
                );
        beverageRepository.delete(beverageEntity);
    }
}
