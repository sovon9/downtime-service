package com.sovon9.downtime_service.entities;

import com.sovon9.downtime_service.dto.ProcessOrder;
import com.sovon9.downtime_service.dto.ProductionUnit;
import com.sovon9.downtime_service.util.GlobalUtil;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "downtime_events")
public class DowntimeEvent implements Node {

    @Transient
    private String id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "downtime_id")
    private Long downtimeId;

    @Column(name = "production_unit_id")
    private Long productionUnitId;

    // Joined locally — production_unit table is in the same shared DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_unit_id", insertable = false, updatable = false)
    private ProductionUnit productionUnit;

    @Column(name = "process_order_id")
    private Long processOrderId;

    // Not a DB join — resolved by the federation gateway from process-order-service
    @Transient
    private ProcessOrder processOrder;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Double durationMinutes;

    @Column(name = "downtime_reason_id")
    private Integer downtimeReasonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downtime_reason_id", insertable = false, updatable = false)
    private DowntimeReason reason;

    @Column(name = "is_planned")
    private Boolean isPlanned;

    @Column(name = "comments")
    private String comments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @PostLoad
    public void postLoad() {
        this.id = GlobalUtil.toGlobalId("DowntimeEvent", downtimeId);
    }

    public Long getDowntimeId() {
        return downtimeId;
    }

    public void setDowntimeId(Long downtimeId) {
        this.downtimeId = downtimeId;
    }

    public Long getProductionUnitId() {
        return productionUnitId;
    }

    public void setProductionUnitId(Long productionUnitId) {
        this.productionUnitId = productionUnitId;
    }

    public ProductionUnit getProductionUnit() {
        return productionUnit;
    }

    public void setProductionUnit(ProductionUnit productionUnit) {
        this.productionUnit = productionUnit;
    }

    public Long getProcessOrderId() {
        return processOrderId;
    }

    public void setProcessOrderId(Long processOrderId) {
        this.processOrderId = processOrderId;
    }

    public ProcessOrder getProcessOrder() {
        return processOrder;
    }

    public void setProcessOrder(ProcessOrder processOrder) {
        this.processOrder = processOrder;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getDowntimeReasonId() {
        return downtimeReasonId;
    }

    public void setDowntimeReasonId(Integer downtimeReasonId) {
        this.downtimeReasonId = downtimeReasonId;
    }

    public DowntimeReason getReason() {
        return reason;
    }

    public void setReason(DowntimeReason reason) {
        this.reason = reason;
    }

    public Boolean getIsPlanned() {
        return isPlanned;
    }

    public void setIsPlanned(Boolean isPlanned) {
        this.isPlanned = isPlanned;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
