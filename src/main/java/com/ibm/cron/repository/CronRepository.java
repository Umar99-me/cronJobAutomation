package com.ibm.cron.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ibm.cron.model.CronModel;

@Repository
public interface CronRepository extends CrudRepository<CronModel, Integer> {

}
