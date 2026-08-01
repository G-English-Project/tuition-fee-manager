package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ActionTypeEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ResourceTypeEnum;

public interface ActionLogService {

    void record(
            ActionTypeEnum action,
            ResourceTypeEnum resourceType,
            Long resourceId,
            String resourceLabel,
            String detail
    );

    default void created(ResourceTypeEnum type, Long id, String label) {
        record(ActionTypeEnum.CREATE, type, id, label, null);
    }

    default void created(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.CREATE, type, id, label, detail);
    }

    default void updated(ResourceTypeEnum type, Long id, String label) {
        record(ActionTypeEnum.UPDATE, type, id, label, null);
    }

    default void updated(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.UPDATE, type, id, label, detail);
    }

    default void deleted(ResourceTypeEnum type, Long id, String label) {
        record(ActionTypeEnum.DELETE, type, id, label, null);
    }

    default void deleted(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.DELETE, type, id, label, detail);
    }

    default void assigned(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.ASSIGN, type, id, label, detail);
    }

    default void removed(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.REMOVE, type, id, label, detail);
    }

    default void restored(ResourceTypeEnum type, Long id, String label) {
        record(ActionTypeEnum.RESTORE, type, id, label, null);
    }

    default void confirmed(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.CONFIRM, type, id, label, detail);
    }

    default void switched(ResourceTypeEnum type, Long id, String label) {
        record(ActionTypeEnum.SWITCH, type, id, label, null);
    }

    default void switched(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.SWITCH, type, id, label, detail);
    }

    default void noted(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.NOTE, type, id, label, detail);
    }

    default void bulkCreated(ResourceTypeEnum type, Long id, String label, String detail) {
        record(ActionTypeEnum.BULK_CREATE, type, id, label, detail);
    }
}
