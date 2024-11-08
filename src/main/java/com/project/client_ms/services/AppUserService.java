package com.project.client_ms.services;

import com.project.client_ms.dtos.AppUserDTO;

import java.util.List;

public interface AppUserService {
    void saveAppUser(AppUserDTO appUserDTO);

    List<AppUserDTO> getAppUsers();
}
