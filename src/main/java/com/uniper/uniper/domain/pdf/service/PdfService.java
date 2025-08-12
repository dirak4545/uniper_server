package com.uniper.uniper.domain.pdf.service;

import com.uniper.uniper.storage.nosql.entity.PdfDocument;
import com.uniper.uniper.storage.nosql.repository.PdfDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {
    
    private final PdfDocumentRepository pdfDocumentRepository;
    
    public List<PdfDocument> getAllPdfDocuments() {
        return pdfDocumentRepository.findAll();
    }
    
    public Optional<PdfDocument> getPdfDocumentById(String id) {
        return pdfDocumentRepository.findById(id);
    }
    
    public List<PdfDocument> getPdfDocumentsByCourseId(Long courseId) {
        return pdfDocumentRepository.findByCourseId(courseId);
    }
    
    public List<PdfDocument> getPdfDocumentsByUploaderId(Long uploaderId) {
        return pdfDocumentRepository.findByUploaderId(uploaderId);
    }
    
    public Optional<PdfDocument> getPdfDocumentByFileName(String fileName) {
        return pdfDocumentRepository.findByFileName(fileName);
    }
    
    public List<PdfDocument> getProcessedPdfDocuments() {
        return pdfDocumentRepository.findByIsProcessed(true);
    }
    
    public List<PdfDocument> getUnprocessedPdfDocuments() {
        return pdfDocumentRepository.findByIsProcessed(false);
    }
    
    public List<PdfDocument> getPdfDocumentsByProcessingStatus(String status) {
        return pdfDocumentRepository.findByProcessingStatus(status);
    }
    
    public List<PdfDocument> getProcessedPdfDocumentsByCourseId(Long courseId) {
        return pdfDocumentRepository.findByCourseIdAndIsProcessed(courseId, true);
    }
    
    public String getPdfFullTextById(String id) {
        return pdfDocumentRepository.findById(id)
                .map(PdfDocument::getFullText)
                .orElse(null);
    }
    
    public List<PdfDocument.PageContent> getPdfPageContentById(String id) {
        return pdfDocumentRepository.findById(id)
                .map(PdfDocument::getPages)
                .orElse(null);
    }
    
    public PdfDocument uploadPdfDocument(MultipartFile file, Long courseId, Long uploaderId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("PDF 파일이 비어있습니다.");
        }
        
        if (!isPdfFile(file)) {
            throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다.");
        }
        
        log.info("PDF 업로드 시작: 파일명={}, 크기={}", file.getOriginalFilename(), file.getSize());
        
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String fullText = textStripper.getText(document);
            int totalPages = document.getNumberOfPages();
            
            List<PdfDocument.PageContent> pages = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                textStripper.setStartPage(i);
                textStripper.setEndPage(i);
                String pageText = textStripper.getText(document);
                pages.add(new PdfDocument.PageContent(i, pageText));
            }
            
            PdfDocument pdfDocument = PdfDocument.builder()
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .totalPages(totalPages)
                    .courseId(courseId)
                    .uploaderId(uploaderId)
                    .fullText(fullText)
                    .pages(pages)
                    .isProcessed(false)
                    .processingStatus("TEXT_EXTRACTED")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            PdfDocument savedDocument = pdfDocumentRepository.save(pdfDocument);
            log.info("PDF 업로드 완료: ID={}, 파일명={}", savedDocument.getId(), savedDocument.getFileName());
            
            return savedDocument;
        } catch (IOException e) {
            log.error("PDF 텍스트 추출 실패: 파일명={}", file.getOriginalFilename(), e);
            throw new IOException("PDF 텍스트 추출에 실패했습니다: " + e.getMessage());
        }
    }
    
    private boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        return "application/pdf".equals(contentType) || 
               (filename != null && filename.toLowerCase().endsWith(".pdf"));
    }
}