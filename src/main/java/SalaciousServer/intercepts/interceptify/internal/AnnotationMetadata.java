package SalaciousServer.intercepts.interceptify.internal;

import java.lang.annotation.Annotation;
import net.bytebuddy.description.annotation.AnnotationDescription;

class AnnotationMetadata {
  private final AnnotationDescription annotation;

  public AnnotationMetadata(AnnotationDescription annotation) {
    this.annotation = annotation;
  }

  public boolean isAnnotation(Class<?> annotationType) {
    return annotation.getAnnotationType().represents(annotationType);
  }

  public <T extends Annotation> T getField(Class<T> annotationType) {
    return annotation.prepare(annotationType).load();
  }
}
