package com.mycompany.project.common.response;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;
@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int pageNumber;      // 현재 페이지 번호 (1부터 시작하도록 조정 가능)
    private final int pageSize;        // 페이지 크기
    private final long totalElements;  // 전체 요소 수
    private final int totalPages;      // 전체 페이지 수
    private final boolean last;        // 마지막 페이지 여부


    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber() + 1; // 1-based index로 변환 (선택사항)
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }


    // 정적 팩토리 메서드
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }
}