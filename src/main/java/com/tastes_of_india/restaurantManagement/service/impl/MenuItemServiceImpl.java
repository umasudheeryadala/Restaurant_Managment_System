package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.service.util.FileUtil;
import com.tastes_of_india.restaurantManagement.domain.Image;
import com.tastes_of_india.restaurantManagement.domain.MenuCategory;
import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import com.tastes_of_india.restaurantManagement.repository.ImageRepository;
import com.tastes_of_india.restaurantManagement.repository.MenuCategoryRepository;
import com.tastes_of_india.restaurantManagement.repository.MenuItemRepository;
import com.tastes_of_india.restaurantManagement.service.MenuItemService;
import com.tastes_of_india.restaurantManagement.service.dto.ImageDTO;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.MenuItemMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final Logger LOG = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    private final String ENTITY_NAME = "menuItemServiceImpl";

    private final MenuItemRepository menuItemRepository;

    private final MenuItemMapper menuItemMapper;

    private final MenuCategoryRepository menuCategoryRepository;

    private final ImageRepository imageRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, MenuItemMapper menuItemMapper, MenuCategoryRepository menuCategoryRepository, ImageRepository imageRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
        this.menuCategoryRepository = menuCategoryRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public MenuItemDTO saveMenuItem(Long restaurantId,MenuItemDTO menuItem, Long categoryId) throws IOException, BadRequestAlertException {

        MenuCategory menuCategory = menuCategoryRepository.findByIdAndRestaurantId(restaurantId,categoryId).orElseThrow(
                () -> new BadRequestAlertException("Menu Category Not Found", ENTITY_NAME, "menuCategoryNotFound")
        );
        menuItem.setName(menuItem.getName() != null ? menuItem.getName().toUpperCase() : null);
        if (menuItem.getId() != null) {
            MenuItem menuItem1 = menuItemRepository.findByIdAndCategoryId(menuItem.getId(),categoryId).orElseThrow(
                    () -> new BadRequestAlertException("Menu Item Not Found", ENTITY_NAME, "menuItemNotFound")
            );
            Set<String> existingImageUrl = menuItem.getImages().stream().map(ImageDTO::getImageUrl).collect(Collectors.toSet());
            List<String> imagesToDelete = new ArrayList<>();
            List<Image> images = new ArrayList<>();
            for (Image image : menuItem1.getImages()) {
                if (!existingImageUrl.contains(image.getImageUrl())) {
                    imagesToDelete.add(image.getImageUrl());
                    image.setMenuItem(null);
                    images.add(image);
                }
            }
            imageRepository.deleteAll(images);
            FileUtil.deleteFiles(imagesToDelete);
            if (menuItem.getName() != null && !menuItem1.getName().equals(menuItem.getName())) {
                Optional<MenuItem> optionalMenuItem = menuItemRepository.findByNameAndCategoryId(menuItem.getName(), categoryId);
                if (optionalMenuItem.isPresent()) {
                    throw new BadRequestAlertException("Menu Item With Name Already Exists", ENTITY_NAME, "menuItemAlreadyExists");
                }
            }
            menuItemMapper.partialUpdate(menuItem1, menuItem);
            return menuItemMapper.toDto(menuItemRepository.save(menuItem1));
        }

        Optional<MenuItem> optionalMenuItem = menuItemRepository.findByNameAndCategoryId(menuItem.getName(), categoryId);

        if (optionalMenuItem.isPresent()) {
            throw new BadRequestAlertException("menu item with name already present", ENTITY_NAME, "menuItemWithNameAlreadyExists");
        }

        MenuItem newMenuItem = menuItemMapper.toEntity(menuItem);
        newMenuItem.setDisabled(false);
        newMenuItem.setMenuCategory(menuCategory);
        newMenuItem.setAvailable(true);
        menuItemRepository.save(newMenuItem);
        return menuItemMapper.toDto(newMenuItem);
    }

    @Override
    public Page<MenuItemDTO> getAllMenuItemsByCategoryId(Long restaurantId,Long categoryId, Boolean disabled,Boolean available,Boolean veg, String pattern, Pageable pageable) throws BadRequestException {
        menuCategoryRepository.findByIdAndRestaurantId(restaurantId,categoryId).orElseThrow(
                () -> new BadRequestException("category with id not present")
        );
        if (pattern != null) pattern = pattern.toLowerCase();
        Page<MenuItem> menuItems = menuItemRepository.findAllByCategoryIdAndDisabledAndPattern(categoryId, disabled,available,veg, pattern, pageable);
        return menuItems.map(menuItemMapper::toDto);
    }

    @Override
    public void uploadImages(Long itemId, List<MultipartFile> multipartFiles) throws IOException, BadRequestAlertException {
        MenuItem menuItem = menuItemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestAlertException("Menu Item Not Found", ENTITY_NAME, "menuItemNotFound")
        );
        Set<Image> images = convertToImage(multipartFiles, menuItem);
        imageRepository.saveAll(images);
        menuItem.setImages(images);
        menuItemRepository.saveAndFlush(menuItem);
    }

    private Set<Image> convertToImage(List<MultipartFile> multipartFiles, MenuItem menuItem) throws IOException {
        List<String> imageUrls = FileUtil.saveFiles(multipartFiles, menuItem.getId());
        Set<Image> images = new HashSet<>();
        for (String imageUrl : imageUrls) {
            Image image = new Image();
            image.setImageUrl(imageUrl);
            image.setMenuItem(menuItem);
            images.add(image);
        }
        return images;
    }

}
