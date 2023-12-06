package com.auto.gen.junit.autoj.dto;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Document(collection = "claz_import_statement")
public class ClazImportStatement {
    private String importStatement;


}
