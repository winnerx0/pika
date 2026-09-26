package com.winnerx0.pika.taxact;

import com.winnerx0.pika.shared.dto.ApiResponse;
import com.winnerx0.pika.taxact.dto.QuestionRequest;
import com.winnerx0.pika.taxact.dto.RecordRequest;

import java.io.IOException;

public interface TaxActService {

    ApiResponse<?> uploadDocument(RecordRequest recordRequest) throws IOException;

    ApiResponse<String> ask(QuestionRequest questionRequest);
}
