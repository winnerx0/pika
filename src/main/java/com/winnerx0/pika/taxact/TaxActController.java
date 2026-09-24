package com.winnerx0.pika.taxact;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.taxact.dto.QuestionRequest;
import com.winnerx0.pika.taxact.dto.RecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tax")
public class TaxActController {

    private final TaxActService taxActService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadTaxDocument(@ModelAttribute RecordRequest recordRequest) throws IOException {
        return ResponseEntity.ok(taxActService.uploadDocument(recordRequest));
    }

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> ask(@RequestBody QuestionRequest questionRequest) {
        return ResponseEntity.ok(taxActService.ask(questionRequest));
    }
}
