package com.uniper.uniper.storage.nosql.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PDF 문서와 추출된 텍스트를 저장하는 MongoDB 문서
 * FastAPI 서버에서 이 데이터를 읽어서 벡터 DB에 저장
 */
@Document(collection = "pdf_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfDocument {

    @Id
    private String id;

    @Field("file_name")
    @Indexed
    private String fileName;

    @Field("file_size")
    private Long fileSize;  // 바이트 단위

    @Field("total_pages")
    private Integer totalPages;

    @Field("course_id")
    @Indexed
    private Long courseId;  // 어떤 과목의 PDF인지 (MySQL Course 테이블 참조)

    @Field("uploader_id")
    private Long uploaderId;  // 업로드한 사용자 ID

    @Field("full_text")
    private String fullText;  // PDF에서 추출한 전체 텍스트

    @Field("pages")
    private List<PageContent> pages;  // 페이지별로 분리된 텍스트

    @Field("is_processed")
    @Builder.Default
    private Boolean isProcessed = false;  // FastAPI에서 벡터화 처리 완료 여부

    @Field("processing_status")
    private String processingStatus;  // UPLOADED, TEXT_EXTRACTED, VECTORIZED, ERROR

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 페이지별 텍스트 내용을 저장하는 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageContent {
        private Integer pageNumber;
        private String text;
        private Integer characterCount;

        public PageContent(Integer pageNumber, String text) {
            this.pageNumber = pageNumber;
            this.text = text;
            this.characterCount = text != null ? text.length() : 0;
        }

        public void setText(String text) {
            this.text = text;
            this.characterCount = text != null ? text.length() : 0;
        }
    }

}