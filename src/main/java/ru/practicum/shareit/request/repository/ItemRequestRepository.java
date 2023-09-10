package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select NEW ru.practicum.shareit.request.model.ItemRequest(r.id, r.description, r.requestorId, r.created) " +
            "from ItemRequest as r " +
            "where r.requestorId = ?1 " +
            "order by r.created desc")
    List<ItemRequest> findByRequestorIdEqualsOrderByCreatedDesc(long userId);

    @Query("select NEW ru.practicum.shareit.request.model.ItemRequest(r.id, r.description, r.requestorId, r.created) " +
            "from ItemRequest as r " +
            "where r.requestorId != ?1 " +
            "order by r.created desc")
    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long userId, Pageable pageable);
}
