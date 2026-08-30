package com.sovon9.downtime_service.controller;

import com.sovon9.downtime_service.entities.DowntimeEvent;
import com.sovon9.downtime_service.repository.DowntimeEventRepository;
import com.sovon9.downtime_service.util.QueryBuilderUtil;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class DowntimeEventGraphqlController {

    private DowntimeEventRepository eventRepository;

    public DowntimeEventGraphqlController(DowntimeEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @QueryMapping("downtimeEvents")
    public Window<DowntimeEvent> downtimeEvents(@Argument Map<String, Object> where, @Argument Map<String, Object> order, ScrollSubrange subrange)
    {
        ScrollPosition scrollPosition = subrange.position().orElse(ScrollPosition.offset());
        int limit = subrange.count().orElse(10);

        Sort.Direction direction = Sort.Direction.ASC;
        if (!subrange.forward()) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = QueryBuilderUtil.buildSort(order, "downtimeId", direction);
        Specification<DowntimeEvent> spec = QueryBuilderUtil.buildSpecification(where);

        if (spec == null) {
            return eventRepository.findBy(scrollPosition, Limit.of(limit), sort);
        }

        return eventRepository.findBy(spec, q -> q.limit(limit).sortBy(sort).scroll(scrollPosition));
    }

    // To do:
    /*
    * select dr1_0.downtime_reason_id,dr1_0.category_id,dr1_0.reason_code,dr1_0.reason_description from downtime_reason dr1_0 where dr1_0.downtime_reason_id=?
    * coming many times, make it batch
     */

}
