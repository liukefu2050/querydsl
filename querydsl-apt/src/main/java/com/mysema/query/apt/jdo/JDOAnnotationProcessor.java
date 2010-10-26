/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.apt.jdo;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.mysema.query.apt.APTException;
import com.mysema.query.apt.DefaultConfiguration;
import com.mysema.query.apt.Processor;

/**
 * AnnotationProcessor for JDO which takes @PersistenceCapable, @EmbeddedOnly and @NotPersistent into account
 * 
 * @author tiwe
 *
 */
@SupportedAnnotationTypes({"com.mysema.query.annotations.*","javax.jdo.annotations.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JDOAnnotationProcessor extends AbstractProcessor{
    
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
            "AS","ASC",
            "ASCENDING","AVG",
            "BY","COUNT",
            "DESC","DESCENDING",
            "DISTINCT","EXCLUDE",
            "FROM","GROUP",
            "HAVING","INTO",
            "MAX","MIN",
            "ORDER","PARAMETERS",
            "RANGE","SELECT",
            "SUBCLASSES","SUM",
            "UNIQUE","VARIABLES",
            "WHERE"));

    private Class<? extends Annotation> entity, embeddable, skip;

    @SuppressWarnings("unchecked")
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());
            entity = (Class)Class.forName("javax.jdo.annotations.PersistenceCapable");
            embeddable = (Class)Class.forName("javax.jdo.annotations.EmbeddedOnly");
            skip = (Class)Class.forName("javax.jdo.annotations.NotPersistent");

            DefaultConfiguration configuration = new DefaultConfiguration(
                    roundEnv, processingEnv.getOptions(), KEYWORDS, null, entity, null, embeddable, null, skip);
            configuration.setUseGetters(false);
            Processor processor = new Processor(processingEnv, roundEnv, configuration);
            processor.process();
            return true;

        } catch (ClassNotFoundException e) {
            throw new APTException(e.getMessage(), e);
        }
    }

}
