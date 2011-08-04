/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.apt.jpa;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.mysema.query.apt.APTException;
import com.mysema.query.apt.DefaultConfiguration;
import com.mysema.query.apt.Processor;

/**
 * AnnotationProcessor for JPA which takes @Entity, @MappedSuperclass, @Embeddable and @Transient into account
 * 
 * @author tiwe
 *
 */
@SupportedAnnotationTypes({"com.mysema.query.annotations.*","javax.persistence.*"})
public class JPAAnnotationProcessor extends AbstractProcessor{

    private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;
    
    protected Class<? extends Annotation> entity, superType, embeddable, embedded, skip;

    @SuppressWarnings("unchecked")
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());
            entity = (Class)Class.forName("javax.persistence.Entity");
            superType = (Class)Class.forName("javax.persistence.MappedSuperclass");
            embeddable = (Class)Class.forName("javax.persistence.Embeddable");
            embedded = (Class)Class.forName("javax.persistence.Embedded");
            skip = (Class)Class.forName("javax.persistence.Transient");

            DefaultConfiguration configuration = createConfiguration(roundEnv);
            Processor processor = new Processor(processingEnv, roundEnv, configuration);
            processor.process();
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;

        } catch (ClassNotFoundException e) {
            throw new APTException(e.getMessage(), e);
        }
    }
    
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    protected DefaultConfiguration createConfiguration(RoundEnvironment roundEnv) throws ClassNotFoundException {
        return new JPAConfiguration(roundEnv, processingEnv.getOptions(), entity, superType, embeddable, embedded, skip);
    }

}
