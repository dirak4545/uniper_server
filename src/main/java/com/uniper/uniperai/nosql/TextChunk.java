package com.uniper.uniperai.nosql;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;

/**
 * PDF 텍스트를 청크 단위로 분할하여 저장
 * FastAPI에서 이 청크들을 읽어서 벡터 임베딩 생성
 */
@Document(collection = "text_chunks")
@CompoundIndexes({
        @CompoundIndex(name = "doc_chunk_idx", def = "{'document_id': 1, 'chunk_index': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextChunk {

    @Id
    private String id;

    @Field("document_id")
    @Indexed
    private String documentId;  // PdfDocument의 ID 참조

    @Field("chunk_index")
    private Integer chunkIndex;  // 청크 순서 (0부터 시작)

    @TextIndexed  // MongoDB 텍스트 검색을 위한 인덱스
    @Field("content")
    private String content;  // 청크 텍스트 내용

    @Field("chunk_size")
    private Integer chunkSize;  // 청크 크기 (문자 수)

    @Field("page_number")
    private Integer pageNumber;  // 원본 PDF의 페이지 번호

    @Field("start_position")
    private Integer startPosition;  // 페이지 내 시작 위치

    @Field("end_position")
    private Integer endPosition;  // 페이지 내 끝 위치

    @Field("course_id")
    @Indexed
    private Long courseId;  // 빠른 검색을 위한 과목 ID

    @Field("is_vectorized")
    @Builder.Default
    private Boolean isVectorized = false;  // FastAPI에서 벡터화 완료 여부

    @Field("vector_id")
    private String vectorId;  // 벡터 DB에 저장된 벡터의 ID (FastAPI에서 업데이트)

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    public void setContent(String content) {
        this.content = content;
        this.chunkSize = content != null ? content.length() : 0;
    }
}