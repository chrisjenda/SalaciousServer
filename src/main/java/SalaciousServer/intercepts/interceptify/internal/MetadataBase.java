package SalaciousServer.intercepts.interceptify.internal;

import java.lang.annotation.Annotation;
import net.bytebuddy.description.annotation.AnnotationList;

abstract class MetadataBase {
  protected abstract AnnotationList getInternal();

  public boolean hasAnnotation(Class<?> annotationType) {
    return getInternal().stream()
        .map(AnnotationMetadata::new)
        .anyMatch(annotation -> annotation.isAnnotation(annotationType));
  }

  public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
    return getInternal().stream()
        .map(AnnotationMetadata::new)
        .filter(m -> m.isAnnotation(annotationType))
        .map(annotation -> annotation.getField(annotationType))
        .findAny()
        .orElse(null);
  }
}
