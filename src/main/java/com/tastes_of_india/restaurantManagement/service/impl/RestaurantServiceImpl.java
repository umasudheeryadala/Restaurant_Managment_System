package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.service.FileService;
import com.tastes_of_india.restaurantManagement.service.mapper.RestaurantEmployeeMapper;
import com.tastes_of_india.restaurantManagement.service.util.storage.LocalStorage;
import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.service.RestaurantService;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantEmployeeDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.RestaurantMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final Logger LOG= LoggerFactory.getLogger(RestaurantService.class);

    private final String ENTITY_NAME="restaurantService";

    private final RestaurantRepository restaurantRepository;

    private final EmployeeService employeeService;

    private final RestaurantMapper restaurantMapper;

    private final RestaurantEmployeeMapper restaurantEmployeeMapper;

    private final FileService fileService;


    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, EmployeeService employeeService, RestaurantMapper restaurantMapper, RestaurantEmployeeMapper restaurantEmployeeMapper, FileService fileService) {
        this.restaurantRepository = restaurantRepository;
        this.employeeService = employeeService;
        this.restaurantMapper = restaurantMapper;
        this.restaurantEmployeeMapper = restaurantEmployeeMapper;
        this.fileService = fileService;
    }

    @Override
    public RestaurantDTO saveRestaurant(RestaurantDTO restaurantDTO) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Employee employee=employeeService.getCurrentlyLoggedInUser();

        if(restaurantDTO.getId()!=null){
            Restaurant restaurant=restaurantRepository.findById(restaurantDTO.getId()).orElseThrow(
                    ()->new BadRequestAlertException("Restaurant With Id Not Found",ENTITY_NAME,"idNotFound")
            );
            restaurantMapper.partialUpdate(restaurant,restaurantDTO);
            if(restaurantDTO.getImage()!=null) {
                fileService.deleteFiles(List.of(restaurant.getLogoUrl()));
                restaurant.setLogoUrl(fileService.saveFiles(List.of(restaurantDTO.getImage()), restaurant.getId()).get(0));
            }
            restaurantRepository.save(restaurant);
            RestaurantEmployeeDTO employeeDTO=new RestaurantEmployeeDTO();
            employeeDTO.setEmployeeId(employee.getId());
            employeeDTO.setDesignation(Designation.OWNER);
            employeeService.saveRestaurantEmployee(restaurant.getId(),employeeDTO);
            return restaurantMapper.toDto(restaurant);
        }
        if(restaurantDTO.getName()==null || restaurantDTO.getName().isEmpty()){
            throw new BadRequestAlertException("Restaurant Name Cannot be Empty",ENTITY_NAME,"nameCannotBeNull");
        }
        Optional<Restaurant> optionalRestaurant=restaurantRepository.findByName(restaurantDTO.getName());

        if(optionalRestaurant.isPresent()){
            throw new BadRequestAlertException("Restaurant with Name Already Exists",ENTITY_NAME,"restaurantNameAlreadyExists");
        }

        Restaurant newRestaurant=new Restaurant();
        newRestaurant.setName(restaurantDTO.getName());;
        newRestaurant.setAddress(restaurantDTO.getAddress());;
        newRestaurant.setType(restaurantDTO.getType());
        newRestaurant.setCreatedBy(employee);
        restaurantRepository.save(newRestaurant);
        if(restaurantDTO.getImage()!=null){
            newRestaurant.setLogoUrl(fileService.saveFiles(List.of(restaurantDTO.getImage()),newRestaurant.getId()).get(0));
        }
        restaurantRepository.save(newRestaurant);
        RestaurantEmployeeDTO restaurantEmployeeDTO=new RestaurantEmployeeDTO();
        restaurantEmployeeDTO.setEmail(employee.getEmail());
        restaurantEmployeeDTO.setDesignation(Designation.OWNER);
        employeeService.saveRestaurantEmployee(newRestaurant.getId(),restaurantEmployeeDTO);
        return restaurantMapper.toDto(restaurantRepository.save(newRestaurant));
    }

    @Override
    public Page<RestaurantDTO> getAllRestaurantByEmployee(Pageable pageable) throws BadRequestAlertException {
        Employee employee=employeeService.getCurrentlyLoggedInUser();
        Page<Restaurant> restaurants=restaurantRepository.findAllByCreatedByIdOrRestaurantEmployeeEmployeeIdIdAndDesignation(employee.getId(),Designation.MANAGER,pageable);
        return restaurants.map(restaurantMapper::toDto);
    }

    @Override
    public RestaurantDTO findByRestaurantById(Long restaurantId) throws BadRequestAlertException {
        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                ()-> new BadRequestAlertException("Restaurant with id not found",ENTITY_NAME,"restaurantNotFound")
        );
        return restaurantMapper.toDto(restaurant);
    }

    @Override
    public void deleteRestaurant(Long restaurantId) throws BadRequestAlertException {
        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                ()-> new BadRequestAlertException("Restaurant with id not found",ENTITY_NAME,"restaurantNotFound")
        );
        restaurant.setDeleted(true);
        restaurantRepository.saveAndFlush(restaurant);
    }
}
