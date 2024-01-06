package com.auto.gen.junit.autoj.translator;

import com.auto.gen.junit.autoj.dto.MyJunitClass;

public interface TranslationManager {

    MyJunitClass startTranslation(MyJunitClass translatedJson, boolean isDtoFlag);
}
