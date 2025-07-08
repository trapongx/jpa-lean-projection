package com.runninglane.jpa.projection.test

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Base class with common utilities for all test cases
 */
@DataJpaTest
@ContextConfiguration
abstract class BaseTest {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager

}
