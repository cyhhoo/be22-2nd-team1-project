package com.mycompany.project.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private String originalFileName; // ?ъ슜?먭? ?щ┛ ?먮옒 ?뚯씪紐?
    private String savedFileName;    // ?쒕쾭????λ맂 ?좊땲?ы븳 ?뚯씪紐?(UUID)
    private String filePath;         // ?뚯씪????λ맂 ?ㅼ젣 寃쎈줈
    private String url;              // 釉뚮씪?곗??먯꽌 ?묎렐?????덈뒗 URL
}