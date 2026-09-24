package com.winnerx0.pika.taxact.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RecordRequest {

    private MultipartFile document;
}
