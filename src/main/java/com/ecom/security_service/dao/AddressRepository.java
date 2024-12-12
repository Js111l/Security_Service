package com.ecom.security_service.dao;

import com.ecom.security_service.dao.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("select a from Address a where a.user.id = :id")
    public List<Address> getUserAddresses(@Param("id") Long userId);
    @Query("select a from Address a where a.user.id = :id and a.isDefault = true and (a.billingAddress = true or a.shippingAddress = true)  order by a.shippingAddress ")
    List<Address> getUserDefaultAddresses(@Param("id") Long userId);
}
