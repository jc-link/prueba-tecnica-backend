package com.project.client_ms.services.impl;

import com.project.client_ms.dtos.AppUserDTO;
import com.project.client_ms.entities.AppUser;
import com.project.client_ms.repositories.AppUserRepository;
import com.project.client_ms.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private static final Logger LOGGER = Logger.getLogger(AppUserServiceImpl.class.getName());
    private final ModelMapper modelMapper;
    private final AppUserRepository appUserRepository;


    @Override
    public void saveAppUser(AppUserDTO appUserDTO) {
        LOGGER.info("Saving user");
        AppUser appUser = mapToEntity(appUserDTO);
        appUserRepository.save(appUser);
    }

    @Override
    public List<AppUserDTO> getAppUsers() {
        LOGGER.info("Fetching all users");
        return appUserRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    private AppUserDTO mapToDTO(AppUser appUser) {
        return modelMapper.map(appUser, AppUserDTO.class);
    }

    private AppUser mapToEntity(AppUserDTO appUserDTO) {
        return modelMapper.map(appUserDTO, AppUser.class);
    }



}
