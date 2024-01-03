package com.auto.gen.junit.autoj.javapoet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@SpringBootTest
@PropertySource("classpath:/application.properties")
class GeneratorHelperTest {
  @InjectMocks
  @Spy
  private GeneratorHelper generatorhelper;

  @Mock
  private Resolver resolver;

  @Mock
  private ParseFile parseFile;

  @Mock
  private SourceCodePathValidator sourceCodePathValidator;

  @BeforeEach
  void setup() {
    EasyRandom easyRandom = new EasyRandom;
         var stringVar_0 = easyRandom.nextObject(String.class);
         var stringVar_1 = easyRandom.nextObject(String.class);
         var MapVar_2= Map.of(stringVar_0,stringVar_1);
         var stringVar_3 = easyRandom.nextObject(String.class);
         var stringVar_4 = easyRandom.nextObject(String.class);
         var MapVar_5= Map.of(stringVar_3,stringVar_4);
  }

  @Test
  void generateTest() {
    Mockito.doNothing().when(resolver.setResolver(Mockito.anyString(),Mockito.anyString()));
        Mockito.when(parseFile.startParsing(Mockito.any())).thenReturn(easyRandom.nextObject(Class.forName(returnType)));
        generate();
        Mockito.verify(generate());
  }

  @Test
  void getAllSourceDirJavaFilesTest() {
    getAllSourceDirJavaFiles();
        Mockito.verify(getAllSourceDirJavaFiles());
  }

  @Test
  void generateTest() {
    Mockito.doNothing().when(resolver.setResolver(Mockito.anyString(),Mockito.anyString()));
        Mockito.when(parseFile.startParsing(Mockito.any())).thenReturn(easyRandom.nextObject(Class.forName(returnType)));
        generate();
        Mockito.verify(generate());
  }
}
