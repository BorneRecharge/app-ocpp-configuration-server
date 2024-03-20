package fr.uge.chargepointconfiguration.shared;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;

/**
 * A utility class to help searching with filters.
 */
public class SearchUtils {

  /**
   * Method that computes the {@link Specification} for JPA, based on a given request.<br>
   * The request given must match the pattern <code>(\w+?)(:|<|>)(\`([^\`]+)\`)</code><br>
   * Here are some examples :
   * <pre>
   *   - key:`value`
   *   - name:`John`,age<`18`
   * </pre><br>
   * Each given parameter must match the name of an attribute of the given entity.<br>
   *
   * @param request A string formatted with a specific pattern to determine the request
   * @param entity The entity to filter on
   * @param <T> The type of the entity
   * @return The JPA specification to use
   * @throws IllegalArgumentException if the request contains fields not declared in the entity
   */
  public static <T> Specification<T> computeSpecification(String request, Class<T> entity) {
    Objects.requireNonNull(request);
    Objects.requireNonNull(entity);

    var params = getSearchCriteria(request);
    checkFieldsInEntity(entity, params);

    var conditions =
        params.stream().<Specification<T>>map(SearchUtils::getSpecification).toList();

    // default condition
    Specification<T> condition = (root, query, criteriaBuilder) -> null;

    for (var newCondition : conditions) {
      condition = condition.and(newCondition);
    }

    return condition;
  }

  private static <T> Specification<T> getSpecification(SearchCriteria criteria) {
    return (root, query, builder) -> switch (criteria.operation()) {
      case MORE_THAN -> {
        if (root.get(criteria.key()).getJavaType() == LocalDateTime.class) {
          yield builder.greaterThanOrEqualTo(
              root.get(criteria.key()).as(LocalDateTime.class),
              LocalDateTime.parse(criteria.value().toString()));
        } else {
          yield builder.greaterThanOrEqualTo(
              root.get(criteria.key()), criteria.value().toString());
        }
      }
      case LESS_THAN -> {
        if (root.get(criteria.key()).getJavaType() == LocalDateTime.class) {
          yield builder.lessThanOrEqualTo(
              root.get(criteria.key()).as(LocalDateTime.class),
              LocalDateTime.parse(criteria.value().toString()));
        } else {
          yield builder.lessThanOrEqualTo(
              root.get(criteria.key()), criteria.value().toString());
        }
      }
      case CONTAINS -> {
        if (root.get(criteria.key()).getJavaType() == String.class) {
          yield builder.like(root.get(criteria.key()), "%" + criteria.value() + "%");
        } else if (root.get(criteria.key()).getJavaType() == LocalDateTime.class) {
          yield builder.equal(
              root.get(criteria.key()).as(LocalDateTime.class),
              LocalDateTime.parse(criteria.value().toString()));
        } else {
          yield builder.equal(root.get(criteria.key()), criteria.value());
        }
      }
      case UNKNOWN -> null;
    };
  }

  private static <T> void checkFieldsInEntity(Class<T> clazz, ArrayList<SearchCriteria> params) {
    params.stream().map(SearchCriteria::key).forEach(field -> {
      try {
        clazz.getDeclaredField(field);
      } catch (NoSuchFieldException e) {
        throw new IllegalArgumentException(
            "Field %s not found in class %s.".formatted(field, clazz.getName()), e);
      }
    });
  }

  private static ArrayList<SearchCriteria> getSearchCriteria(String request) {
    var pattern =
        Pattern.compile("(\\w+?)(:|<|>)(\\`([^\\`]+)\\`)", Pattern.UNICODE_CHARACTER_CLASS);
    var matcher = pattern.matcher(request + ",");
    var params = new ArrayList<SearchCriteria>();

    while (matcher.find()) {
      params.add(new SearchCriteria(
          matcher.group(1),
          SearchCriteria.Operation.fromString(matcher.group(2)),
          matcher.group(4)));
    }
    return params;
  }
}
