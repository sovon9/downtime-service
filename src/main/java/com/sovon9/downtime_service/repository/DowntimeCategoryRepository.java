package com.sovon9.downtime_service.repository;

import com.sovon9.downtime_service.entities.DowntimeCategory;
import com.sovon9.downtime_service.entities.DowntimeEvent;
import com.sovon9.downtime_service.entities.DowntimeReason;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DowntimeCategoryRepository extends JpaRepository<DowntimeCategory, Integer>, JpaSpecificationExecutor<DowntimeCategory> {
    Window<DowntimeEvent> findBy(ScrollPosition position, Limit limit, Sort sort);
}
