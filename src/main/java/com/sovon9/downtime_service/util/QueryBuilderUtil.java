package com.sovon9.downtime_service.util;

import com.sovon9.downtime_service.entities.DowntimeEvent;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryBuilderUtil {

    public static Sort buildSort(Map<String, Object> order, String defaultField, Sort.Direction defaultDirection) {
        if (order == null || order.isEmpty()) {
            return Sort.by(defaultDirection, defaultField);
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Map.Entry<String, Object> entry : order.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            // Handle nested objects in sorting (e.g. reason: {reasonCode: "ASC"})
            if (value instanceof Map) {
                Map<String, Object> nestedOrder = (Map<String, Object>) value;
                for (Map.Entry<String, Object> nestedEntry : nestedOrder.entrySet()) {
                    String nestedField = field + "." + nestedEntry.getKey();
                    String directionStr = String.valueOf(nestedEntry.getValue());

                    Sort.Direction direction;
                    if (defaultDirection == Sort.Direction.DESC) {
                        direction = "DESC".equalsIgnoreCase(directionStr) ? Sort.Direction.ASC : Sort.Direction.DESC;
                    } else {
                        direction = "DESC".equalsIgnoreCase(directionStr) ? Sort.Direction.DESC : Sort.Direction.ASC;
                    }
                    orders.add(new Sort.Order(direction, nestedField));
                }
            } else {
                String directionStr = String.valueOf(value);

                Sort.Direction direction;
                if (defaultDirection == Sort.Direction.DESC) {
                    direction = "DESC".equalsIgnoreCase(directionStr) ? Sort.Direction.ASC : Sort.Direction.DESC;
                } else {
                    direction = "DESC".equalsIgnoreCase(directionStr) ? Sort.Direction.DESC : Sort.Direction.ASC;
                }
                orders.add(new Sort.Order(direction, field));
            }
        }

        return Sort.by(orders);
    }

    public static Specification<DowntimeEvent> buildSpecification(Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = buildPredicates(filter, root, criteriaBuilder);

            if (predicates.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @SuppressWarnings("unchecked")
    private static List<Predicate> buildPredicates(
            Map<String, Object> filter,
            jakarta.persistence.criteria.Root<DowntimeEvent> root,
            jakarta.persistence.criteria.CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            if (key.equals("and")) {
                List<Map<String, Object>> andList = (List<Map<String, Object>>) value;
                List<Predicate> andPredicates = new ArrayList<>();
                for (Map<String, Object> andFilter : andList) {
                    andPredicates.addAll(buildPredicates(andFilter, root, cb));
                }
                if (!andPredicates.isEmpty()) {
                    predicates.add(cb.and(andPredicates.toArray(new Predicate[0])));
                }

            } else if (key.equals("or")) {
                List<Map<String, Object>> orList = (List<Map<String, Object>>) value;
                List<Predicate> orPredicates = new ArrayList<>();
                for (Map<String, Object> orFilter : orList) {
                    List<Predicate> innerOrPredicates = buildPredicates(orFilter, root, cb);
                    if (!innerOrPredicates.isEmpty()) {
                        orPredicates.add(cb.and(innerOrPredicates.toArray(new Predicate[0])));
                    }
                }
                if (!orPredicates.isEmpty()) {
                    predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
                }

            } else if (key.equals("id") || key.equals("downtimeId")) {
                try {
                    Long parsedId = Long.parseLong(value.toString());
                    predicates.add(cb.equal(root.get("downtimeId"), parsedId));
                } catch (NumberFormatException e) {
                    try {
                        String[] globalId = GlobalUtil.fromGlobalId(value.toString());
                        if (globalId.length == 2) {
                            predicates.add(cb.equal(root.get("downtimeId"), Long.parseLong(globalId[1])));
                        }
                    } catch (Exception ex) {
                        // ignore invalid global IDs
                    }
                }

            } else if (value instanceof Map) {
                // Nested filter — supports two levels deep:
                //
                //   Level 1:  reason: { reasonCode: "BOTTLE_JAM" }
                //             → LEFT JOIN downtime_reason ON ... WHERE reason_code = 'BOTTLE_JAM'
                //
                //   Level 2:  reason: { category: { categoryName: "Mechanical" } }
                //             → LEFT JOIN downtime_reason ON ...
                //               LEFT JOIN downtime_category ON ... WHERE category_name = 'Mechanical'
                //
                // Using LEFT JOIN keeps DowntimeEvents with NULL reason_id in results
                // when filtering on other simple fields at the same time.
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                jakarta.persistence.criteria.Join<?, ?> firstJoin = root.join(key, JoinType.LEFT);

                for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                    String nestedKey   = nestedEntry.getKey();
                    Object nestedValue = nestedEntry.getValue();

                    if (nestedValue == null) {
                        continue;
                    }

                    if (nestedValue instanceof Map) {
                        // Level 2: e.g. category: { categoryName: "Mechanical" }
                        Map<String, Object> deepMap = (Map<String, Object>) nestedValue;
                        jakarta.persistence.criteria.Join<?, ?> secondJoin = firstJoin.join(nestedKey, JoinType.LEFT);

                        for (Map.Entry<String, Object> deepEntry : deepMap.entrySet()) {
                            if (deepEntry.getValue() != null) {
                                predicates.add(cb.equal(secondJoin.get(deepEntry.getKey()), deepEntry.getValue()));
                            }
                        }
                    } else {
                        // Level 1: e.g. reasonCode: "BOTTLE_JAM"
                        predicates.add(cb.equal(firstJoin.get(nestedKey), nestedValue));
                    }
                }

            } else {
                // Simple field: isPlanned, productionUnitId, processOrderId, comments, etc.
                predicates.add(cb.equal(root.get(key), value));
            }
        }

        return predicates;
    }
}