package com.auto.gen.junit.autoj.translator;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TranslateToEasyRandom {

    AtomicInteger count;

    public BuildSetUpMethod getBuildSetUpMethod() {
        return buildSetUpMethod;
    }

    public void setBuildSetUpMethod(BuildSetUpMethod buildSetUpMethod) {
        this.buildSetUpMethod = buildSetUpMethod;
    }

    private BuildSetUpMethod buildSetUpMethod;
    TranslateToEasyRandom(){
        buildSetUpMethod = new BuildSetUpMethod();
        buildSetUpMethod.setUp.append("EasyRandom easyRandom = new EasyRandom;");
        count = new AtomicInteger();
    }
    @Translate
    public String getString(){
        String varName = "stringVar_"+count.getAndIncrement();
        buildSetUpMethod.getSetUp().append("\n")
                .append(varName+" = easyRandom.nextObject(String.class);").append("\n");
        return varName;
    }
    @Translate
    public String getList(String type, int size){
        String varName = "ListVar_"+count.getAndIncrement();
        String claz = "Class.forName("+type+")";
        buildSetUpMethod.getSetUp().append(varName+ " = easyRandom.objects(").append(claz + ","+ size+");").append("\n");
        return varName;
    }

    public String getMap(Object key, Object value, int size){
        String varName = "MapVar_"+count.getAndIncrement();
        buildSetUpMethod.getSetUp().append(varName+ "= ").append("Map.of("+key+","+value+");");
        return varName;
    }

    public String getSet(String type, int size){
        String varName = "SetVar_"+count.getAndIncrement();
        String claz = "Class.forName("+type+")";
        buildSetUpMethod.getSetUp().append(varName+ " = easyRandom.objects(").append(claz + ","+ size+");").append("\n");
        return varName;
    }

    public String investigate(String returnType){
        if(returnType.contains("java.lang.String")){
            return getString();
        }
        else if(returnType.contains("void") || returnType.contains("Void"))
            return "doNothing()";
        else {
            int index = returnType.lastIndexOf(".");
            if (index == -1) {
                return String.format("easyRandom.nextObject(%s.class)",returnType);
            } else {
                return String.format("easyRandom.nextObject(Class.forName(%s.class))",returnType.substring(index+1));
            }
//            return String.format("easyRandom.nextObject(Class.forName(%s))",returnType);
        }
    }


}
