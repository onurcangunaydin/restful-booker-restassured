package com.example.booker.tests;

import com.example.booker.client.BookerClient;
import com.example.booker.config.ApiConfig;
import com.example.booker.model.AuthRequest;
import com.example.booker.model.AuthResponse;
import com.example.booker.model.BookingDates;
import com.example.booker.model.BookingIdItem;
import com.example.booker.model.BookingRequest;
import com.example.booker.model.BookingResponse;
import com.example.booker.model.CreatedBookingResponse;
import com.example.booker.model.PartialBookingUpdateRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookerApiTest {

    private BookerClient client;
    private String token;

    @BeforeClass
    public void setUp() {
        client = new BookerClient();
        token = client.createToken(new AuthRequest(ApiConfig.username(), ApiConfig.password()))
                .as(AuthResponse.class)
                .token();
    }

    @Test
    public void pingShouldReturnCreated() {
        Response response = client.ping();

        response.then().statusCode(201);
    }

    @Test
    public void authShouldReturnToken() {
        Response response = client.createToken(new AuthRequest(ApiConfig.username(), ApiConfig.password()));

        response.then().statusCode(200);
        AuthResponse authResponse = response.as(AuthResponse.class);
        assertThat(authResponse.token()).isNotBlank();
    }

    @Test
    public void getBookingIdsShouldReturnAtLeastOneId() {
        Response response = client.getBookingIds();

        response.then().statusCode(200);
        List<BookingIdItem> ids = response.as(new TypeRef<>() {
        });
        assertThat(ids).isNotEmpty();
        assertThat(ids.get(0).bookingid()).isNotNull();
    }

    @Test
    public void getBookingByIdShouldReturnCreatedBooking() {
        int bookingId = createBookingAndGetId();

        Response response = client.getBookingById(bookingId);

        response.then().statusCode(200);
        BookingResponse booking = response.as(BookingResponse.class);
        assertThat(booking.firstname()).isNotBlank();
        assertThat(booking.lastname()).isNotBlank();
    }

    @Test
    public void createBookingShouldReturnCreatedBookingIdAndPayload() {
        BookingRequest request = randomBookingRequest();

        Response response = client.createBooking(request);

        response.then().statusCode(200);
        CreatedBookingResponse created = response.as(CreatedBookingResponse.class);

        assertThat(created.bookingid()).isNotNull().isPositive();
        assertThat(created.booking().firstname()).isEqualTo(request.firstname());
        assertThat(created.booking().lastname()).isEqualTo(request.lastname());
        assertThat(created.booking().additionalneeds()).isEqualTo(request.additionalneeds());
    }

    @Test
    public void updateBookingShouldReplaceExistingBooking() {
        int bookingId = createBookingAndGetId();
        BookingRequest updateRequest = new BookingRequest(
                "UpdatedName",
                "UpdatedLast",
                777,
                false,
                new BookingDates("2026-03-01", "2026-03-07"),
                "Late checkout"
        );

        Response response = client.updateBooking(bookingId, updateRequest, token);

        response.then().statusCode(200);
        BookingResponse updated = response.as(BookingResponse.class);
        assertThat(updated.firstname()).isEqualTo(updateRequest.firstname());
        assertThat(updated.lastname()).isEqualTo(updateRequest.lastname());
        assertThat(updated.totalprice()).isEqualTo(updateRequest.totalprice());
    }

    @Test
    public void partialUpdateBookingShouldUpdateOnlyGivenFields() {
        int bookingId = createBookingAndGetId();
        PartialBookingUpdateRequest patchRequest = new PartialBookingUpdateRequest("PatchName", "Breakfast");

        Response response = client.partialUpdateBooking(bookingId, patchRequest, token);

        response.then().statusCode(200);
        BookingResponse patched = response.as(BookingResponse.class);
        assertThat(patched.firstname()).isEqualTo(patchRequest.firstname());
        assertThat(patched.additionalneeds()).isEqualTo(patchRequest.additionalneeds());
    }

    @Test
    public void deleteBookingShouldReturnCreatedAndThenBookingShouldNotExist() {
        int bookingId = createBookingAndGetId();

        Response deleteResponse = client.deleteBooking(bookingId, token);
        deleteResponse.then().statusCode(201);

        Response getDeleted = client.getBookingById(bookingId);
        getDeleted.then().statusCode(404);
    }

    private int createBookingAndGetId() {
        Response response = client.createBooking(randomBookingRequest());
        response.then().statusCode(200);
        return response.as(CreatedBookingResponse.class).bookingid();
    }

    private BookingRequest randomBookingRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDate checkin = LocalDate.now().plusDays(1);
        LocalDate checkout = checkin.plusDays(2);

        return new BookingRequest(
                "Api" + suffix,
                "Test" + suffix,
                250,
                true,
                new BookingDates(checkin.toString(), checkout.toString()),
                "Dinner"
        );
    }
}
