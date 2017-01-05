/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.bibliome.util.defaultmap.DefaultMap;

/**
 * Annotation processor for the Service and Services annotations.
 * This processor generates the appropriate META-INF/services files.
 * @author rbossy
 *
 */
@SupportedAnnotationTypes( {
    "org.bibliome.util.service.Service",
    "org.bibliome.util.service.Services"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceAnnotationProcessor extends AbstractProcessor {
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;
	private TypeElement serviceAnnotation;
	private TypeElement servicesAnnotation;

	@Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
		elementUtils = procEnv.getElementUtils();
        filer = procEnv.getFiler();
        messager = procEnv.getMessager();
		serviceAnnotation = elementUtils.getTypeElement(Service.class.getCanonicalName());
		servicesAnnotation = elementUtils.getTypeElement("org.bibliome.util.service.Services");
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver())
			return false;
		DefaultMap<TypeMirror,Collection<TypeElement>> services = new DefaultMap<TypeMirror,Collection<TypeElement>>(true, new HashMap<TypeMirror,Collection<TypeElement>>()) {
			@Override
			protected Collection<TypeElement> defaultValue(TypeMirror key) {
				return new HashSet<TypeElement>();
			}
		};
		messager.printMessage(Diagnostic.Kind.NOTE, "scanning " + serviceAnnotation.getQualifiedName().toString());
		for (TypeElement serviceElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(Service.class))) {
			messager.printMessage(Diagnostic.Kind.NOTE, "  class: " + serviceElement.getQualifiedName().toString());
			Set<Modifier> modifiers = serviceElement.getModifiers();
			if (!modifiers.contains(Modifier.PUBLIC)) {
				messager.printMessage(Diagnostic.Kind.ERROR, "  should be public");
				continue;
			}
			if (modifiers.contains(Modifier.ABSTRACT)) {
				messager.printMessage(Diagnostic.Kind.ERROR, "  should not be abstract");
				continue;
			}
			boolean hasDefaultCtor = false;
			for (ExecutableElement constructor : ElementFilter.constructorsIn(serviceElement.getEnclosedElements())) {
				if (constructor.getModifiers().contains(Modifier.PUBLIC) && constructor.getParameters().isEmpty()) {
					hasDefaultCtor = true;
					break;
				}
			}
			if (!hasDefaultCtor) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "  should have a public default constructor");
				continue;
			}
			for (AnnotationMirror ann : serviceElement.getAnnotationMirrors()) {
				DeclaredType annType = ann.getAnnotationType();
				if (!annType.asElement().equals(processingEnv.getElementUtils().getTypeElement(Service.class.getCanonicalName())))
					continue;
				for (Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : ann.getElementValues().entrySet()) {
					if (!"value".equals(e.getKey().getSimpleName().toString()))
						continue;
					TypeMirror annVal = (TypeMirror) e.getValue().getValue();
					services.safeGet(annVal).add(serviceElement);
					break;
				}
			}
		}
		messager.printMessage(Diagnostic.Kind.NOTE, "scanning " + servicesAnnotation.getQualifiedName().toString());
		for (TypeElement serviceElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(servicesAnnotation))) {
			messager.printMessage(Diagnostic.Kind.NOTE, "  class: " + serviceElement.getQualifiedName().toString());
			for (AnnotationMirror ann : serviceElement.getAnnotationMirrors()) {
				DeclaredType annType = ann.getAnnotationType();
				if (!annType.asElement().equals(processingEnv.getElementUtils().getTypeElement(Services.class.getCanonicalName())))
					continue;
				for (Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : ann.getElementValues().entrySet()) {
					if (!"value".equals(e.getKey().getSimpleName().toString()))
						continue;
					for (Object annVal : (List<Object>) e.getValue().getValue())
						services.safeGet((TypeMirror) annVal).add(serviceElement);
					break;
				}
			}
		}
		try {
			for (Map.Entry<TypeMirror,Collection<TypeElement>> e : services.entrySet()) {
				TypeMirror serv = e.getKey();
			    FileObject fo = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" + serv, (Element[])null);
				messager.printMessage(Diagnostic.Kind.NOTE, "writing " + fo.toUri());
			    PrintWriter out = new PrintWriter(fo.openWriter());
			    for (TypeElement impl : e.getValue())
			    	out.println(impl.getQualifiedName());
			    out.close();
			}
		} catch (IOException ioe) {
			messager.printMessage(Diagnostic.Kind.ERROR, ioe.getMessage());
		}
		return true;
	}
}
