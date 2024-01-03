package com.auto.gen.junit.autoj.translator;

import com.auto.gen.junit.autoj.dto.JunitMethod;
import com.auto.gen.junit.autoj.dto.MyJunitClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransformerToTranslator implements TranslationManager {

    @Autowired
    TranslateToMockito translateToMockito;

    /**
     * @param translatedJson
     */
    @Override
    public MyJunitClass startTranslation(MyJunitClass translatedJson) {
        TranslateToEasyRandom translateToEasyRandom = new TranslateToEasyRandom();
        addMandatoryImports(translatedJson);
        if(ObjectUtils.isEmpty(translatedJson.getMethodList())){
            return null;
        }
        for (JunitMethod methods : translatedJson.getMethodList()) {
            setMethodParameters(methods);
            Map<String, List<String>> toMockMap = methods.getMockObjects().getMockObjectList();
            List<String> thirdPartyMethodDetails = new LinkedList<>();
            List<List<String>> allStatementsTobeMocked = new LinkedList<>();
            String thirdPartyClass = null;
            for (String key : toMockMap.keySet()) {
                if (key.contains("if"))
                    continue;
                if (key.substring(0, key.indexOf("(")).contains("."))
                    thirdPartyClass = key.substring(0, key.indexOf("."));
                else
                    thirdPartyClass = translatedJson.getClassName();
                thirdPartyMethodDetails = toMockMap.get(key).stream().collect(Collectors.toList());
                allStatementsTobeMocked.add(thirdPartyMethodDetails);

                for (List<String> toMockStatement : allStatementsTobeMocked) {
                    String mockArgument = testMethodArguments(toMockStatement.get(0));
                    String mockReturnType = testMethodReturnType(toMockStatement.get(1), translateToEasyRandom);
                    List<String> mocks = new LinkedList<>();
                    System.out.println("mockArg = " + thirdPartyClass + "." + mockArgument + " mockReturn = " + mockReturnType);
                    mocks.add(thirdPartyClass + "." + mockArgument);
                    mocks.add(mockReturnType);
                    toMockMap.replace(key, mocks);
                }
            }
        }
        translatedJson.setPreTestConfiguration(translateToEasyRandom.getBuildSetUpMethod().getSetUp().toString());
        return translatedJson;
    }

    private void addMandatoryImports(MyJunitClass translatedJson) {
        translatedJson.getImportStatementList().add("java.util.Map");
        translatedJson.getImportStatementList().add("java.util.Set");
        translatedJson.getImportStatementList().add("java.util.List");
        translatedJson.getImportStatementList().add("org.mockito.Mockito");
//        translatedJson.getImportStatementList().add("org.jeasy.random.EasyRandom");
    }

    private String testMethodReturnType(String methodReturnType, TranslateToEasyRandom translateToEasyRandom) {
        if (methodReturnType.startsWith("java.util.Map")) {
            String[] parsedParentStr = methodReturnType.split("<");
            String parentType = parsedParentStr[0];
            String[] parsedChildStr = parsedParentStr[1].substring(0, parsedParentStr[1].indexOf(">")).split(",");
            String leftChild = testMethodReturnType(parsedChildStr[0], translateToEasyRandom); // process the left child.
            String rightChild = testMethodReturnType(parsedChildStr[1], translateToEasyRandom); // process the right child.
            return translateToEasyRandom.getMap(leftChild, rightChild, 0);
        } else if (methodReturnType.startsWith("java.util.Set")) {
            String[] parsedParentStr = methodReturnType.split("<");
            String type = parsedParentStr[1].substring(0, parsedParentStr[1].indexOf(">") - 1);
            return translateToEasyRandom.getSet(type, 1);
        } else if (methodReturnType.startsWith("java.util.List")) {
            String[] parsedParentStr = methodReturnType.split("<");
            String type = parsedParentStr[1].substring(0, parsedParentStr[1].indexOf(">"));
            return translateToEasyRandom.getSet(type, 1);
        } else
            return translateToEasyRandom.investigate(methodReturnType);
    }

    private String testMethodArguments(String methodSignature) {
        String argumentTypes = methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")"));
        System.out.println("argumentTypes =" + argumentTypes);
        String methodName = methodSignature.substring(0, methodSignature.indexOf("("));
        System.out.println("methodName = " + methodName);
        if (argumentTypes.contains(",")) {
            StringBuilder builder = new StringBuilder();
            String[] parameters = argumentTypes.split(",");
            for (String params : parameters) {
                if (!builder.isEmpty())
                    builder.append(",");
                builder.append(mockMethodInvocation(params).orElseThrow());
            }
            return methodName + "(" + builder.toString() + ")";
        }
        Optional<String> mockMethodArgInvoke = mockMethodInvocation(argumentTypes);
        return mockMethodArgInvoke.map(s -> methodName + "(" + s + ")").orElse(("No such Type found"));
    }

    private void setMethodParameters(JunitMethod methods) {
        List<String> methodTobeTestedParameters = methods.getMethodToBeTestedParameters().stream()
                .map(e -> mockMethodInvocation(e))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        methods.setMethodToBeTestedParameters(methodTobeTestedParameters);

    }

    private Optional<String> mockMethodInvocation(String annotationValue) {
        TranslateToMockito translateToMockito = new TranslateToMockito();
        Method[] methods = translateToMockito.getClass().getDeclaredMethods();
        Optional<String> mockString = Arrays.stream(methods).filter(method -> method.isAnnotationPresent(Translate.class)).map(method ->
        {
            System.out.println("annotationValue :: " + annotationValue);
            if (annotationValue.contains("java.lang.String")) {
                return (String) translateToMockito.getStringMock();
            } else if (annotationValue.contains("java.lang.Boolean")) {
                return (String) translateToMockito.getBooleanMock();
            } else{
            return (String) translateToMockito.getAnyMock();
        }
        }).findAny();
        return mockString;
    }

}
