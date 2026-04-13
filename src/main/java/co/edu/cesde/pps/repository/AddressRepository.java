package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    long countByUser_UserId(Long userId);

    List<Address> findByUser_UserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Address a set a.isDefault = false where a.user.userId = :userId")
    void unsetDefaultByUserId(@Param("userId") Long userId);
}