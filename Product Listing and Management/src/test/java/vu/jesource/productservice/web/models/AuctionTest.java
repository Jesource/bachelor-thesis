package vu.jesource.productservice.web.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

class AuctionTest {
    private static final long ONE_HOUR_IN_MILLISECONDS = 3600_000;
    Auction auction;

    @BeforeEach
    void setUp() {
        auction = new Auction(
                new Date(System.currentTimeMillis() + ONE_HOUR_IN_MILLISECONDS),   // start date
                new Date(System.currentTimeMillis() + 2 * ONE_HOUR_IN_MILLISECONDS) // end date
        );
    }

    @Test
    void givenValidStartDate_whenSetStartDate_thenSuccess() {
        auction.setStartDate(new Date(System.currentTimeMillis() + 100_000));
    }

    @Test
    void givenInvalidStartDate_whenSetStartDate_thenFailure() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> auction.setStartDate(new Date(System.currentTimeMillis() - 1000))
        );
    }

    @Test
    void givenValidEndDate_whenSetEndDate_thenSuccess() {
        auction.setEndDate(new Date(auction.getStartDate().getTime() + Auction.MIN_AUCTION_DURATION_TIME_IN_MS));
    }

    @Test
    void givenEndDateFromPast_whenSetEndDate_thenFailure() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> auction.setEndDate(new Date(System.currentTimeMillis() - 1000))
        );
    }

//    @Test
//    void givenDurationTimeInBetweenMaxAndMinAuctionDurationTime_whenSetDurationInMs_thenSuccess() {
//        auction.setDurationInMs(Auction.MIN_AUCTION_DURATION_TIME_IN_MS + 1000);
//    }
//
//    @Test
//    void givenDurationTimeLessThanMinAuctionDurationTime_whenSetDurationInMs_thenFailure() {
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> auction.setDurationInMs(Auction.MIN_AUCTION_DURATION_TIME_IN_MS - 1000)
//        );
//    }
//
//    @Test
//    void givenDurationTimeMoreThanMaxAuctionDurationTime_whenSetDurationInMs_thenFailure() {
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> auction.setDurationInMs(Auction.MAX_AUCTION_DURATION_TIME_IN_MS + 1000)
//        );
//    }
}