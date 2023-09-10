package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select * from requests where requestor_id = ? order by created desc;")
    List<ItemRequest> findAllByRequestorIdEqualsOrderByCreatedDesc(long userId);

    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long userId, Pageable pageable);
}
