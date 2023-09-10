package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    LocalDateTime dt = LocalDateTime.now();
    ItemDto testItemDto1 = new ItemDto(1, "predmet", "prosto predmet", true, null, 1);
    ItemForUpdate testItemDtoUpdate1 = new ItemForUpdate("NEWpredmet", "prosto predmet", true, null, 1);
    ItemDto updateTest = new ItemDto(1, "NEWpredmet", "prosto predmet", true, null, 1);
    User user1 = new User(1L, "user1", "user1@ya.ru");
    UserDto testUserDto1 = new UserDto(1, "user1", "user1@ya.ru");
    Item testItem1 = new Item(1L, "predmet", "prosto predmet", true, null, user1);
    CommentDto testCommentDto = new CommentDto(
            1,
            "predmet prosto vayyyy",
            testItemDto1.getId(),
            testUserDto1.getName(),
            dt.toString());
    Booking testBooking1 = new Booking(
            1L,
            dt,
            dt.plusDays(1),
            testItem1,
            user1,
            BookingStatus.WAITING);
    ItemCommentDto testItemCommentDto1 = new ItemCommentDto(
            1L,
            "predmet",
            "prosto predmet",
            true, 1L,
            BookingMapper.toBookingForItemDto(testBooking1),
            null,
            List.of());

    @Test
    void createItemTest_ifValid_returnItemDto() {
//        ItemDto itemDto = testItemDto1;
//        itemDto.setId(0L);
        when(itemRepository.save(testItem1)).thenReturn(testItem1);
        when(userRepository.findById(testUserDto1.getId())).thenReturn(Optional.ofNullable(user1));
        ItemDto result = itemService.createItem(testItemDto1, testUserDto1.getId());

        assertEquals(testItemDto1, result);
    }

    @Test
    void createItemTest_ifIncorrectRequestId_returnValidationException() {
        ItemDto testItemDto2 = testItemDto1;
        testItemDto2.setRequestId(3L);
        long requestId = 3;

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.createItem(testItemDto2, testUserDto1.getId()));
    }

    @Test
    void createItemTest_ifNoSuchUser_returnNotFoundException() {
        ItemDto testItemDto2 = testItemDto1;
        testItemDto2.setOwnerId(3);
        long incorrectUserId = 3;

        when(userRepository.findById(incorrectUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(testItemDto2, incorrectUserId));
    }

    @Test
    void updateItemTest_ifValid_returnUpdatedItemDto() {
        long userId = 1;
        long itemId = 1;
        Item updateItem = ItemMapper.toItem(updateTest);
        updateItem.setId(itemId);
        updateItem.setOwner(user1);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(testItem1));
        when(itemRepository.save(updateItem)).thenReturn(updateItem);

        ItemDto updated = itemService.updateItem(testItemDtoUpdate1, itemId, userId);

        assertEquals(updated, ItemMapper.toItemDto(updateItem));
    }

    @Test
    void updateItemTest_ifNoSuchOwner_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(testItemDtoUpdate1, 1, userId));
    }

    @Test
    void updateItemTest_ifNoSuchItem_NotFoundException() {
        long itemId = 0;
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(testItemDtoUpdate1, itemId, 1));
    }

    @Test
    void getItemTest_ifOk_returnItemCommentDto() {
        long itemId = 1;
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(testItem1));
        doReturn(testBooking1).when(bookingRepository).findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                itemId,
                BookingStatus.REJECTED,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        doReturn(null).when(bookingRepository).findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                itemId,
                BookingStatus.REJECTED,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        when(commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(itemId)).thenReturn(List.of());

        ItemCommentDto itemCommentDto = itemService.getItem(itemId, userId);

        assertEquals(testItemCommentDto1, itemCommentDto);
    }

    @Test
    void getItemTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1, userId));
    }

    @Test
    void getItemTest_ifNoSuchItem_NotFoundException() {
        long itemId = 0;
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, 1));
    }

    @Test
    void getItemsTest_ifOk_returnItemCommentDtoList() {
        long itemId = 1;
        long userId = 1;
        Pageable pageParams = PageRequest.of(fromToPage(0, 10), 10, Sort.by(Sort.Direction.ASC, "id"));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findAllByOwnerIdIsOrderByIdAsc(userId, pageParams)).thenReturn(new PageImpl<>(List.of(testItem1)));
        doReturn(testBooking1).when(bookingRepository).findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                itemId,
                BookingStatus.REJECTED,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        doReturn(null).when(bookingRepository).findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                itemId,
                BookingStatus.REJECTED,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        when(commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(itemId)).thenReturn(List.of());

        List<ItemCommentDto> itemCommentDto = itemService.getItems(userId, 0, 10);

        assertEquals(List.of(testItemCommentDto1), itemCommentDto);
    }

    @Test
    void getItemsTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItems(userId, 0, 10));
    }

    @Test
    void searchItemsTest_ifOk_returnItemDtoList() {
        String text = "1";
        Pageable pageParams = PageRequest.of(fromToPage(0, 10), 10, Sort.by(Sort.Direction.ASC, "id"));
        when(itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageParams))
                .thenReturn(new PageImpl<>(List.of(testItem1)));

        List<ItemDto> itemDtoList = itemService.searchItems(text, 0, 10);

        assertEquals(List.of(testItemDto1), itemDtoList);
    }

    @Test
    void createCommentTest_ifOk_returnCommentDto() {
        long itemId = 1;
        long userId = 1;
        Comment testComment = CommentMapper.toComment(testCommentDto);
        testComment.setAuthor(user1);
        testComment.setItem(testItem1);
        testComment.setCreated(LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
//        MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class);
//        mockedStatic.when(LocalDateTime::now).thenReturn(dt);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(testItem1));
        when(bookingRepository.findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(
                userId,
                itemId,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())))
                .thenReturn(testBooking1);
        Comment testComment2 = new Comment(
                1L,
                "predmet prosto vayyyy",
                testItem1,
                UserMapper.mapToNewUser(testUserDto1),
                dt);
        when(commentRepository.save(testComment)).thenReturn(testComment2);

        CommentDto commentDto = itemService.createComment(testCommentDto, itemId, userId);

        assertEquals(CommentMapper.toCommentDto(testComment2), commentDto);
    }

    @Test
    void createCommentTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(testCommentDto, 1, userId));
    }

    @Test
    void createCommentTest_ifNoSuchItem_NotFoundException() {
        long itemId = 0;
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(testCommentDto, itemId, 1));
    }

    @Test
    void createCommentTest_ifNotBooker_NoAccessException() {
        long itemId = 1;
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem1));
        when(bookingRepository.findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(
                userId,
                1,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())))
                .thenReturn(null);

        assertThrows(NoAccessException.class, () -> itemService.createComment(testCommentDto, 1, userId));
    }

    private int fromToPage(int from, int size) {
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
