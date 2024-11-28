package com.project.client_ms.controller;

import com.project.client_ms.dtos.ClientDTO;
import com.project.client_ms.services.ClientService;
import com.project.client_ms.utils.constants.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.ENDPOINT_CLIENTS)
public class ClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getClients() {
        LOGGER.info("Fetching all clients");
        return ResponseEntity.ok(clientService.getClients());
    }

    @PostMapping("/upload")
    public ResponseEntity<List<ClientDTO>> saveClients(@RequestParam("file") MultipartFile file) {

        LOGGER.info("Saving clients from Excel file");
        List<ClientDTO> clients = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Skip header row
                    continue;
                }
                ClientDTO clientDTO = new ClientDTO();
                clientDTO.setNombre(getCellValue(row, 0));
                clientDTO.setRut(getCellValue(row, 1));
                clientDTO.setAddress(getCellValue(row, 2));
                clientDTO.setPhone(getCellValue(row, 3));
                clients.add(clientDTO);
            }
        } catch (Exception e) {
            LOGGER.severe("Error reading Excel file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        clientService.saveClients(clients);
        return ResponseEntity.noContent().build();
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return cell == null ? "" : cell.getStringCellValue();
    }

}
