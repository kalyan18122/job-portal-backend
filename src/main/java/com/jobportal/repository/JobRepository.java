package com.jobportal.repository;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedByOrderByCreatedAtDesc(User postedBy);

    @Query("""
        SELECT j FROM Job j
        WHERE (:keyword IS NULL OR :keyword = '' OR
               LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:location IS NULL OR :location = '' OR
               LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:jobType IS NULL OR :jobType = '' OR j.jobType = :jobType)
        ORDER BY j.createdAt DESC
        """)
    List<Job> search(@Param("keyword") String keyword,
                      @Param("location") String location,
                      @Param("jobType") String jobType);
}
