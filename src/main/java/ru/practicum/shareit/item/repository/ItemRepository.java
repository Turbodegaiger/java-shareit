package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerIdIsOrderByIdAsc(long userId, Pageable pageable);

    Page<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String text1, String text2, Pageable pageable);

    List<ItemForResponseDto> findAllByRequestIdEquals(long id);
}
