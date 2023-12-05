package com.auto.gen.junit.autoj.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection="ParsedClasses")
public class ParsedClass {

    @Id
    private Long id;
    private String ClassName;
    private String payload;

    private Date createdDate;

    private Long version;

}
