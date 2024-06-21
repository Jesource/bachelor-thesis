package vu.jesource.productservice.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Slf4j
public class Auction extends Product {
    public static final long MIN_AUCTION_DURATION_TIME_IN_MS = 3600_000;   // 1 hour in ms
    public static final long MAX_AUCTION_DURATION_TIME_IN_MS = 259_200_000;    // 30 days in ms
    private Date startDate;
    private Date endDate;
    private long durationInMs;
    private double instantPurchasePrice = -1;

    public Auction(Date startDate, Date endDate) throws NullPointerException {
        setStartDate(startDate);
        setEndDate(endDate);
        durationInMs = getDurationInMs();
    }

    public Auction(String id, String title, String description, Conditions condition, double price,
                   Set<OptionalParameter> optionalParameters, Date startDate, Date endDate, double instantPurchasePrice,
                   UserDetails creator) {
        super(id, title, description, condition, price, optionalParameters, creator);
        setStartDate(startDate);
        setEndDate(endDate);
        setInstantPurchasePrice(instantPurchasePrice);
        durationInMs = getDurationInMs();
    }

    private static void validateDurationTime(long durationInMs) {
        if (durationInMs < MIN_AUCTION_DURATION_TIME_IN_MS) {
            throw new IllegalArgumentException(
                    String.format(
                            "Auction duration cannot be shorter than %s seconds",
                            MIN_AUCTION_DURATION_TIME_IN_MS / 1000));
        }

        if (durationInMs > MAX_AUCTION_DURATION_TIME_IN_MS) {
            throw new IllegalArgumentException(String.format(
                    "Auction duration cannot be longer than %s seconds",
                    MAX_AUCTION_DURATION_TIME_IN_MS / 1000));
        }
    }

    public void setInstantPurchasePrice(double instantPurchasePrice) {
        if (instantPurchasePrice <= 0) {
            throw new IllegalArgumentException("Instant purchase price cannot be for free");
        }

        this.instantPurchasePrice = instantPurchasePrice;
    }

    public long getDurationInMs() {
        return endDate.getTime() - startDate.getTime();
    }

    public void setStartDate(Date startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be empty");
        }

        if (!isNewStartDateAcceptable(startDate)) {
            throw new IllegalArgumentException("Start date cannot be accepted: with this new start date auction would already have ended");
        }

        this.startDate = startDate;
    }

    /**
     * Checks if auction will end before current time if new auction start date is accepted
     *
     * @param startDate must be in the future
     * @return true if new date can be accepted, otherwise false
     */
    private boolean isNewStartDateAcceptable(Date startDate) {
        boolean startDateIsNotInThePast = startDate.after(new Date(System.currentTimeMillis()));

        return startDateIsNotInThePast;
    }

    public void setEndDate(Date endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Start date cannot be empty");
        }

        if (endDate.before(new Date(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("New end date cannot be in the past");
        }

        this.endDate = endDate;
    }


    @Override
    public String toString() {
        return super.toString() + " Auction specifics{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationInMs=" + durationInMs +
                '}';
    }
}
