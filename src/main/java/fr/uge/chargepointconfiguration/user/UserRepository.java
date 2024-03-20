package fr.uge.chargepointconfiguration.user;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the user.
 */
@Repository
public interface UserRepository
    extends CrudRepository<User, Integer>,
        PagingAndSortingRepository<User, Integer>,
        JpaSpecificationExecutor<User> {

  /**
   * Returns a User from the database according to the email.
   *
   * @param email User's email.
   * @return The correct User or null if the user couldn't be found.
   */
  User findByEmail(String email);

  User findById(int id);

  List<User> findAll();

  Page<User> findAll(Pageable pageable);
}
