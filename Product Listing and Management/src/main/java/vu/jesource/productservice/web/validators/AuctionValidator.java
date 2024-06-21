package vu.jesource.productservice.web.validators;

import vu.jesource.productservice.web.models.Auction;

import java.util.Date;

public class AuctionValidator extends ProductValidator {
    
    public static void validate(Auction auction) throws IllegalArgumentException {
        ProductValidator.validate(auction);

        validateDates(auction.getStartDate(), auction.getEndDate());
        validateStartDate(auction.getStartDate());
        validateEndDate(auction.getEndDate());
        validateDuration(auction.getDurationInMs());
        validateInstantPurchasePrice(auction.getInstantPurchasePrice());
    }

    private static void validateInstantPurchasePrice(double instantPurchasePrice) {
        if (instantPurchasePrice <= 0) {
            throw new IllegalArgumentException("Instant purchase price cannot be for free");
        }
    }

    private static void validateDuration(long durationInMs) {
        if (durationInMs > Auction.MAX_AUCTION_DURATION_TIME_IN_MS || durationInMs < Auction.MIN_AUCTION_DURATION_TIME_IN_MS) {
            throw new IllegalArgumentException("Auction duration happens to be too short or too long");
        }
    }

    private static void validateDates(Date startDate, Date endDate) {
        validateStartDate(startDate);
        validateEndDate(endDate);

        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private static void validateEndDate(Date endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be empty");
        }

        if (endDate.after(new Date(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }

    }

    private static void validateStartDate(Date startDate) throws IllegalArgumentException {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be empty");
        }
        
        if (startDate.after(new Date(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
}
