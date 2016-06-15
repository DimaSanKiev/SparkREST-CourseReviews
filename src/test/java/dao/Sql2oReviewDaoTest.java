package dao;

import exc.DaoException;
import model.Course;
import model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

public class Sql2oReviewDaoTest {

    private Sql2oCourseDao courseDao;
    private Sql2oReviewDao reviewDao;
    private Connection conn;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        conn = sql2o.open();
        courseDao = new Sql2oCourseDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        course = new Course("Test", "http://test.com");
        courseDao.add(course);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingReviewSetsNewId() throws Exception {
        Review review = newTestReview(10, "Test comment");
        review.setCourseId(course.getId());
        int originalReviewId = review.getId();

        reviewDao.add(review);

        assertNotEquals(originalReviewId, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistForACourse() throws Exception {
        reviewDao.add(newTestReview(5, "Test comment 1"));
        reviewDao.add(newTestReview(1, "Test comment 1"));

        List<Review> reviews = reviewDao.findByCourseId(course.getId());

        assertEquals(2, reviews.size());
    }

    @Test(expected = DaoException.class)
    public void addingAReviewToANonExistingCourseFails() throws Exception {
        Review review = new Review(42, 5, "Test comment");

        reviewDao.add(review);
    }

    private Review newTestReview(int rating, String comment) {
        return new Review(course.getId(), rating, comment);
    }
}