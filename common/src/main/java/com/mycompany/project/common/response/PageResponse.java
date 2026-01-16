package com.mycompany.project.common.response;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;
@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int pageNumber;      // ?꾩옱 ?섏씠吏 踰덊샇 (1遺???쒖옉?섎룄濡?議곗젙 媛??
    private final int pageSize;        // ?섏씠吏 ?ш린
    private final long totalElements;  // ?꾩껜 ?붿냼 ??
    private final int totalPages;      // ?꾩껜 ?섏씠吏 ??
    private final boolean last;        // 留덉?留??섏씠吏 ?щ?


    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber() + 1; // 1-based index濡?蹂??(?좏깮?ы빆)
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }


    // ?뺤쟻 ?⑺넗由?硫붿꽌??
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }
}