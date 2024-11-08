package com.project.client_ms.services;

import com.project.client_ms.dtos.ClientDTO;
import com.project.client_ms.entities.Client;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getClients();

    void saveClients(List<ClientDTO> clients);
}
