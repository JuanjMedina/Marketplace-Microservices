package com.marketplace.productservice.service;

import com.marketplace.productservice.controller.dto.ProductFilterCriteria;
import com.marketplace.productservice.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductSpecification {

    public static Specification<Product> findByCriteria(ProductFilterCriteria criteria) {
        // La expresión lambda recibe 3 argumentos de la API de Criteria de JPA:
        // root: representa la entidad raíz (Product).
        // query: la consulta que se está construyendo.
        // criteriaBuilder: un constructor para crear predicados (condiciones WHERE).
        return (root, query, criteriaBuilder) -> {

            // Creamos una lista para almacenar todos nuestros predicados (condiciones).
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro por 'category'
            if (criteria.category() != null && !criteria.category().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), criteria.category()));
            }

            // 2. Filtro por 'inStock'
            // Si inStock es 'true', queremos productos con quantity > 0.
            // Si inStock es 'false', queremos productos con quantity = 0.
            // Si inStock es 'null', no filtramos por este campo.
            if (criteria.inStock() != null) {
                if (criteria.inStock()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("quantity"), 0));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("quantity"), 0));
                }
            }

            // 3. Filtro por 'minPrice'
            if (criteria.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.minPrice()));
            }

            // 4. Filtro por 'maxPrice'
            if (criteria.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
            }

            // Combinamos todos los predicados con un AND.
            // Si la lista está vacía, devuelve una condición siempre verdadera.
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}