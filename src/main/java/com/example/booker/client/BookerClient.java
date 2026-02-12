package com.example.booker.client;

import com.example.booker.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BookerClient {

    private final RequestSpecification spec;

    public BookerClient() {
        this.spec = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public Response ping() {
        return RestAssured.given(spec)
                .when()
                .get("/ping");
    }

    public Response createToken(Object authRequest) {
        return RestAssured.given(spec)
                .body(authRequest)
                .when()
                .post("/auth");
    }

    public Response getBookingIds() {
        return RestAssured.given(spec)
                .when()
                .get("/booking");
    }

    public Response getBookingById(int bookingId) {
        return RestAssured.given(spec)
                .when()
                .get("/booking/{id}", bookingId);
    }

    public Response createBooking(Object bookingRequest) {
        return RestAssured.given(spec)
                .body(bookingRequest)
                .when()
                .post("/booking");
    }

    public Response updateBooking(int bookingId, Object updateRequest, String token) {
        return RestAssured.given(spec)
                .header("Cookie", "token=" + token)
                .body(updateRequest)
                .when()
                .put("/booking/{id}", bookingId);
    }

    public Response partialUpdateBooking(int bookingId, Object patchRequest, String token) {
        return RestAssured.given(spec)
                .header("Cookie", "token=" + token)
                .body(patchRequest)
                .when()
                .patch("/booking/{id}", bookingId);
    }

    public Response deleteBooking(int bookingId, String token) {
        return RestAssured.given(spec)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId);
    }
}
