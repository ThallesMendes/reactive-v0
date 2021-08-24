package com.reactive.demo.mapping;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResultMapperRelations {

  public <T extends Entity<?>> T mapper (T previous, T current) {
    final var relations = Arrays
        .stream(previous.getClass().getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(OneToMany.class))
        .collect(Collectors.toSet());

    relations.forEach(relation -> {
      final var previousList = this.callGet(previous, relation);
      final var currentList = this.callGet(current, relation);

      final var currentItem = currentList.stream().findFirst().orElseThrow();

      previousList.stream()
          .filter(p -> p.getId().equals(currentItem.getId()))
          .findFirst()
          .ifPresentOrElse(item -> this.mapper(item, currentItem),
                           () -> previousList.addAll(this.callGet(current, relation)));

      this.callSet(previous, relation, previousList);
    });

    return previous;
  }

  @SuppressWarnings("unchecked")
  private <T extends Entity<?>> ArrayList<Entity<?>> callGet(
      final T object, final Field field) {
      final var property = getProperty(object, field);
      try {
        return new ArrayList<>((Collection<Entity<?>>) property.getReadMethod().invoke(object));
      } catch (Exception ex) {
        return new ArrayList<>();
      }
  }

  private <T extends Entity<?>> void callSet(final T object, final Field field, final Object value) {
    final var property = getProperty(object, field);
    try {
      property.getWriteMethod().invoke(object, value);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private <T extends Entity<?>> PropertyDescriptor getProperty(final T object, final Field field) {
    try {
      return Arrays.stream(Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors())
          .filter(p -> Objects.nonNull(p.getReadMethod()))
          .filter(p -> p.getName().equals(field.getName()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("property not found"));

    } catch (IntrospectionException e) {
      throw new IllegalArgumentException("error in search property");
    }
  }

}
