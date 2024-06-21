package vu.jesource.productservice.web.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import vu.jesource.productservice.web.models.Auction;

public interface AuctionRepository extends MongoRepository<Auction, String> {
}
