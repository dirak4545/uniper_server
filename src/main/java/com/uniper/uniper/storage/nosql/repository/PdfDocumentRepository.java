package com.uniper.uniper.storage.nosql.repository;

import com.uniper.uniper.storage.nosql.entity.PdfDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdfDocumentRepository extends MongoRepository<PdfDocument, String> {
    
    List<PdfDocument> findByCourseId(Long courseId);
    
    List<PdfDocument> findByUploaderId(Long uploaderId);
    
    Optional<PdfDocument> findByFileName(String fileName);
    
    List<PdfDocument> findByIsProcessed(Boolean isProcessed);
    
    List<PdfDocument> findByProcessingStatus(String processingStatus);
    
    List<PdfDocument> findByCourseIdAndIsProcessed(Long courseId, Boolean isProcessed);
}