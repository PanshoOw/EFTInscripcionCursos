package com.duoc.cloudnative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoc.cloudnative.entity.Resumen;

@Repository
public interface ResumenRepository extends JpaRepository<Resumen, Long> {
    
}
