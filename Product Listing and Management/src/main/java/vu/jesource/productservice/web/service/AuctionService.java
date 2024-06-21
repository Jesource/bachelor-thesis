package vu.jesource.productservice.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.jesource.productservice.web.exceptions.CrudOperationException;
import vu.jesource.productservice.web.exceptions.UserAuthorisationException;
import vu.jesource.productservice.web.models.Auction;
import vu.jesource.productservice.web.models.Product;
import vu.jesource.productservice.web.models.Product.UserDetails;
import vu.jesource.productservice.web.repos.AuctionRepository;
import vu.jesource.productservice.web.validators.AuctionValidator;

import java.util.Optional;

@Slf4j
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }


    private static void checkIfAuctionExist(boolean dbProductCheck,
                                            String id, CrudOperationException.Operations crudOperation) {
        if (dbProductCheck) {
            log.info("Failed to {} auction with id '{}': auction does not exist", crudOperation, id);

            throw new CrudOperationException(
                    crudOperation,
                    String.format("Auction with id '%s' was not found", id));
        }
    }

    public Auction save(Auction auction) throws IllegalArgumentException {
        AuctionValidator.validate(auction);

        return auctionRepository.save(auction);
    }

    public Object getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Auction getAuctionById(String id) throws CrudOperationException {
        Optional<Auction> dbAuction = auctionRepository.findById(id);

        checkIfAuctionExist(dbAuction.isEmpty(), id, CrudOperationException.Operations.READ);

        return auctionRepository.findById(id).get();

    }

    public void deleteById(String id, UserDetails user) throws CrudOperationException {
        log.info("Deleting product with id '{}'", id);

        checkIfAuctionExist(!auctionRepository.existsById(id), id, CrudOperationException.Operations.DELETE);
        checkIfUserIsTheCreator(id, user);

        auctionRepository.deleteById(id);
    }

    public Product update(Auction auction, String id, UserDetails user) throws CrudOperationException {
        Optional<Auction> savedProduct = auctionRepository.findById(auction.getId());

        checkIfAuctionExist(savedProduct.isEmpty(), id, CrudOperationException.Operations.UPDATE);
        checkIfUserIsTheCreator(id, user);

        return auctionRepository.save(auction);
    }

    private void checkIfUserIsTheCreator(String id, UserDetails user) throws CrudOperationException, UserAuthorisationException {
        if (!isTheUserIsTheCreator(id, user)) {
            throw new UserAuthorisationException("Looks like you are not the creator of this auction with id " + id);
        }
    }

    private boolean isTheUserIsTheCreator(String id, UserDetails user) {
        Optional<Auction> dbAuction = auctionRepository.findById(id);

        if (dbAuction.isPresent()) {
            UserDetails actualProductCreator = dbAuction.get().getCreator();

            boolean isTheUserIsTheCreator = actualProductCreator.equals(user);
            log.info("Is the user is the creator: {}", isTheUserIsTheCreator);

            return isTheUserIsTheCreator;
        }
        log.info("User {} trying to delete auction with id '{}' is not the auction's creator", user, id);
        return false;
    }
}
