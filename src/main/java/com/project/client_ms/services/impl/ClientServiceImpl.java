package com.project.client_ms.services.impl;

import com.project.client_ms.dtos.ClientDTO;
import com.project.client_ms.entities.Client;
import com.project.client_ms.repositories.ClientRepository;
import com.project.client_ms.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final Logger LOGGER = Logger.getLogger(ClientServiceImpl.class.getName());

    private final ClientRepository clientRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<ClientDTO> getClients() {
        LOGGER.info("Fetching all clients");
        return clientRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public void saveClients(List<ClientDTO> clients) {
        LOGGER.info("Saving clients");
        List<Client> clientEntities = clients.stream().map(this::mapToEntity).toList();
        clientRepository.saveAll(clientEntities);
    }

    private ClientDTO mapToDTO(Client client) {
        return modelMapper.map(client, ClientDTO.class);
    }

    private Client mapToEntity(ClientDTO clientDTO) {
        return modelMapper.map(clientDTO, Client.class);
    }
}
