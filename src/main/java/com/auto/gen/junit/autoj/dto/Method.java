package com.auto.gen.junit.autoj.dto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.mapping.Document;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Setter
@Getter
@Document(collection = "method")
public class Method {
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public List<Parameter> getMethodParameters() {
        return methodParameters;
    }

    public void setMethodParameters(List<Parameter> methodParameters) {
        this.methodParameters = methodParameters;
    }

    public MethodBody getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(MethodBody methodBody) {
        this.methodBody = methodBody;
    }

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
