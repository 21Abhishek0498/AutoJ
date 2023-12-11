package com.auto.gen.junit.autoj.exclusions;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageLevelExclusions {

    List<String> exclusions = List.of("java.lang","java.util");
    public boolean isPackageExcluded(String fullyQualifiedName){
      return  exclusions.stream().anyMatch(pkg -> fullyQualifiedName.startsWith(pkg));
    }
}
