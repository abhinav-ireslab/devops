/**
 * 
 */
package com.ireslab.coinclaim.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.coinclaim.entity.UniqueIndex;

/**
 * @author iRESlab
 *
 */
public interface UniqueAddressRepo extends CrudRepository<UniqueIndex, Long> {

}
