package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.MenuCategory;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.repository.MenuCategoryRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.MenuCategoryService;
import com.tastes_of_india.restaurantManagement.service.dto.MenuCategoryDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.MenuCategoryMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final Logger LOG= LoggerFactory.getLogger(MenuCategoryService.class);

    private final String ENTITY_NAME="MenuCategoryService";

    private final MenuCategoryRepository menuCategoryRepository;

    private final MenuCategoryMapper menuCategoryMapper;

    private final RestaurantRepository restaurantRepository;

    public MenuCategoryServiceImpl(MenuCategoryRepository menuCategoryRepository, MenuCategoryMapper menuCategoryMapper, RestaurantRepository restaurantRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuCategoryMapper = menuCategoryMapper;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public MenuCategoryDTO saveMenuCategory(Long restaurantId,MenuCategoryDTO menuCategoryDTO) throws BadRequestAlertException {

        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant With Id Not Found",ENTITY_NAME,"restaurantNotFound")
        );

        menuCategoryDTO.setName(menuCategoryDTO.getName().toUpperCase());

        if(menuCategoryDTO.getId()!=null){
            MenuCategory menuCategory=menuCategoryRepository.findByIdAndRestaurantId(menuCategoryDTO.getId(),restaurantId).orElseThrow(
                    ()-> new BadRequestAlertException("Menu Category Not Found",ENTITY_NAME,"menuCategoryNotFound")
            );
            if(!menuCategory.getName().equals(menuCategoryDTO.getName())){
                Optional<MenuCategory> optionalMenuCategory=menuCategoryRepository.findByNameAndRestaurantId(menuCategoryDTO.getName(),restaurantId);
                if(optionalMenuCategory.isPresent()){
                    throw new BadRequestAlertException("category with name already present",ENTITY_NAME,"categoryWithNameAlready");
                }
            }
            menuCategoryMapper.partialUpdate(menuCategory,menuCategoryDTO);
            return menuCategoryMapper.toDto(menuCategoryRepository.saveAndFlush(menuCategory));
        }

        Optional<MenuCategory> optionalMenuCategory=menuCategoryRepository.findByNameAndRestaurantId(menuCategoryDTO.getName(),restaurantId);
        if(optionalMenuCategory.isPresent()){
            throw new BadRequestAlertException("category with name already present",ENTITY_NAME,"categoryWithNameAlready");
        }
        MenuCategory menuCategory= menuCategoryMapper.toEntity(menuCategoryDTO);
        menuCategory.setRestaurant(restaurant);
        menuCategory.setDisabled(false);
        menuCategoryRepository.save(menuCategory);
        return menuCategoryMapper.toDto(menuCategory);
    }

    @Override
    public Page findAllMenuCategories(Pageable pageable, String pattern) {

        if(pattern!=null) pattern=pattern.toLowerCase();

        return menuCategoryRepository.findAllByPattern(pattern,pageable).map(menuCategoryMapper::toDto);
    }

    @Override
    public Page<MenuCategoryDTO> findAllMenuCategories(Long restaurantId,Boolean disabled, Pageable pageable) throws BadRequestAlertException {
        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Id Not Found",ENTITY_NAME,"restaurantNotFound")
        );
        Page<MenuCategory> menuCategories=null;
        if(disabled!=null) {
             menuCategories = menuCategoryRepository.findByRestaurantIdAndDisabled(restaurant.getId(), disabled, pageable);
        }else{
            menuCategories =menuCategoryRepository.findByRestaurantId(restaurant.getId(),pageable);
        }
        return menuCategories.map(menuCategoryMapper::toDto);
    }
}
