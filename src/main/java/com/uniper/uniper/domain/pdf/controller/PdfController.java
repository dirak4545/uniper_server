package com.uniper.uniper.domain.pdf.controller;

import com.uniper.uniper.domain.pdf.service.PdfService;
import com.uniper.uniper.storage.nosql.entity.PdfDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {
    
    private final PdfService pdfService;
    
    @GetMapping
    public ResponseEntity<List<PdfDocument>> getAllPdfDocuments() {
        List<PdfDocument> documents = pdfService.getAllPdfDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PdfDocument> getPdfDocumentById(@PathVariable String id) {
        return pdfService.getPdfDocumentById(id)
                .map(document -> ResponseEntity.ok(document))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<PdfDocument>> getPdfDocumentsByCourseId(@PathVariable Long courseId) {
        List<PdfDocument> documents = pdfService.getPdfDocumentsByCourseId(courseId);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<PdfDocument>> getPdfDocumentsByUploaderId(@PathVariable Long uploaderId) {
        List<PdfDocument> documents = pdfService.getPdfDocumentsByUploaderId(uploaderId);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/filename/{fileName}")
    public ResponseEntity<PdfDocument> getPdfDocumentByFileName(@PathVariable String fileName) {
        return pdfService.getPdfDocumentByFileName(fileName)
                .map(document -> ResponseEntity.ok(document))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/processed")
    public ResponseEntity<List<PdfDocument>> getProcessedPdfDocuments() {
        List<PdfDocument> documents = pdfService.getProcessedPdfDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/unprocessed")
    public ResponseEntity<List<PdfDocument>> getUnprocessedPdfDocuments() {
        List<PdfDocument> documents = pdfService.getUnprocessedPdfDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PdfDocument>> getPdfDocumentsByStatus(@PathVariable String status) {
        List<PdfDocument> documents = pdfService.getPdfDocumentsByProcessingStatus(status);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/course/{courseId}/processed")
    public ResponseEntity<List<PdfDocument>> getProcessedPdfDocumentsByCourseId(@PathVariable Long courseId) {
        List<PdfDocument> documents = pdfService.getProcessedPdfDocumentsByCourseId(courseId);
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/{id}/text")
    public ResponseEntity<String> getPdfFullText(@PathVariable String id) {
        String fullText = pdfService.getPdfFullTextById(id);
        if (fullText != null) {
            return ResponseEntity.ok(fullText);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/pages")
    public ResponseEntity<List<PdfDocument.PageContent>> getPdfPageContent(@PathVariable String id) {
        List<PdfDocument.PageContent> pages = pdfService.getPdfPageContentById(id);
        if (pages != null) {
            return ResponseEntity.ok(pages);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("uploaderId") Long uploaderId) {
        
        try {
            PdfDocument document = pdfService.uploadPdfDocument(file, courseId, uploaderId);
            return ResponseEntity.ok(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("PDF 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}