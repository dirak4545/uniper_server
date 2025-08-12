package com.uniper.uniper.domain.pdf.controller;

import com.uniper.uniper.domain.pdf.service.PdfService;
import com.uniper.uniper.storage.nosql.entity.PdfDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "PDF 문서 관리", description = "PDF 파일 업로드, 조회, 텍스트 추출 관련 API")
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {
    
    private final PdfService pdfService;
    
    @Operation(summary = "모든 PDF 문서 조회", description = "시스템에 저장된 모든 PDF 문서를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                    content = @Content(schema = @Schema(implementation = PdfDocument.class)))
    })
    @GetMapping
    public ResponseEntity<List<PdfDocument>> getAllPdfDocuments() {
        List<PdfDocument> documents = pdfService.getAllPdfDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @Operation(summary = "PDF 문서 단건 조회", description = "ID로 특정 PDF 문서를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PdfDocument> getPdfDocumentById(
            @Parameter(description = "PDF 문서 ID", required = true) @PathVariable String id) {
        return pdfService.getPdfDocumentById(id)
                .map(document -> ResponseEntity.ok(document))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "과목별 PDF 문서 조회", description = "특정 과목에 속한 PDF 문서들을 조회합니다.")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<PdfDocument>> getPdfDocumentsByCourseId(
            @Parameter(description = "과목 ID", required = true) @PathVariable Long courseId) {
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
    
    @Operation(summary = "PDF 전체 텍스트 조회", description = "PDF 문서의 전체 텍스트 내용을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음")
    })
    @GetMapping("/{id}/text")
    public ResponseEntity<String> getPdfFullText(
            @Parameter(description = "PDF 문서 ID", required = true) @PathVariable String id) {
        String fullText = pdfService.getPdfFullTextById(id);
        if (fullText != null) {
            return ResponseEntity.ok(fullText);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "PDF 페이지별 텍스트 조회", description = "PDF 문서의 페이지별 텍스트 내용을 조회합니다.")
    @GetMapping("/{id}/pages")
    public ResponseEntity<List<PdfDocument.PageContent>> getPdfPageContent(
            @Parameter(description = "PDF 문서 ID", required = true) @PathVariable String id) {
        List<PdfDocument.PageContent> pages = pdfService.getPdfPageContentById(id);
        if (pages != null) {
            return ResponseEntity.ok(pages);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "PDF 파일 업로드", description = "PDF 파일을 업로드하고 텍스트를 추출하여 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공", 
                    content = @Content(schema = @Schema(implementation = PdfDocument.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 형식 오류 등)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPdf(
            @Parameter(description = "업로드할 PDF 파일", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "과목 ID", required = true) @RequestParam("courseId") Long courseId,
            @Parameter(description = "업로더 사용자 ID", required = true) @RequestParam("uploaderId") Long uploaderId) {
        
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