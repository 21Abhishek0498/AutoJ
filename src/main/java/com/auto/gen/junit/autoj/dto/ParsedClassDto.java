package com.auto.gen.junit.autoj.dto;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParsedClassDto {

    private String ClassName;
    private String payload;

    private Date createdDate;

    private Long version;
}
