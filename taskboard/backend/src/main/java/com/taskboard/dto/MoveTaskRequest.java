package com.taskboard.dto;

public class MoveTaskRequest {
    private Long targetColumnId;
    private Integer targetSortOrder;

    public Long getTargetColumnId() { return targetColumnId; }
    public void setTargetColumnId(Long targetColumnId) { this.targetColumnId = targetColumnId; }
    public Integer getTargetSortOrder() { return targetSortOrder; }
    public void setTargetSortOrder(Integer targetSortOrder) { this.targetSortOrder = targetSortOrder; }
}
