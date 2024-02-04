package io.certivity.backend.repository;

import io.certivity.backend.model.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {

    Page findByUrlAndSort(String url,int sort);
    List<Page> findByUrlOrderBySortAsc(String url);

}
