package com.auto.gen.junit.autoj.dto;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Builder
@Data
@Setter
@Getter
public class Method {
    String methodName;
    Type returnType;
    String accessModifier;
    List<Parameter> methodParameters;
    MethodBody methodBody;

    @Builder
    @Getter
    @Setter
    public static class MethodBody {
        private Optional<BlockStmt> methodBody;

    }
}
