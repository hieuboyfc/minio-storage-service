package com.minio.storage.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface XBaseRepo<E, ID extends Serializable> extends JpaRepository<E, ID> {
}
