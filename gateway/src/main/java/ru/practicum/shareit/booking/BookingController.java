package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.validation.BookingValidator;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private final String userIdHeader = "X-Sharer-User-Id";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingShortDto bookingDto,
	                                            @RequestHeader(userIdHeader) long userId) {
		BookingValidator.validateBookingShortDto(bookingDto);
		log.info("Принят запрос на создание booking {} от пользователя id = {}", bookingDto, userId);
		return bookingClient.createBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> approveOrDenyBooking(@PathVariable long bookingId,
	                                                   @RequestParam @NotNull Boolean approved,
	                                                   @RequestHeader(userIdHeader) long userId) {
		log.info("Принят запрос на подтверждение бронирования id = {} от пользователя id = {}, approved = {}.",
				bookingId, userId, approved);
		return bookingClient.approveOrDenyBooking(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
	                             @RequestHeader(userIdHeader) long userId) {
		log.info("Принят запрос на получение booking id = {} от пользователя id = {}", bookingId, userId);
		return bookingClient.getBooking(bookingId, userId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getUserBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
	                                        @RequestHeader(userIdHeader) long userId,
	                                        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
	                                        @Positive @RequestParam(defaultValue = "10", required = false) int size) {
		BookingState stateParam = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Принят запрос на получение бронирований для пользователя id = {}. Параметры запроса:" +
				"stateParam = {}, from = {}, size = {}.", userId, stateParam, from, size);
		return bookingClient.getAllUserBookings(userId, stateParam, from, size);
	}

	@GetMapping("/owner")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
	                                         @RequestHeader(userIdHeader) long userId,
	                                         @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
	                                         @Positive @RequestParam(defaultValue = "10", required = false) int size) {
		BookingState stateParam = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Принят запрос на получение бронирований для пользователя-владельца id = {}. Параметры запроса:" +
				"stateParam = {}, from = {}, size = {}.", userId, stateParam, from, size);
		return bookingClient.getAllOwnerBookings(userId, stateParam, from, size);
	}
}
