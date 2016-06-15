import com.google.gson.Gson;
import dao.Sql2oCourseDao;
import model.Course;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;
import testing.ApiClient;
import testing.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiTest {

    public static final String PORT = "4567";
    public static final String TEST_DATASOURCE = "h2:mem:testing";
    private Connection conn;
    private ApiClient client;
    private Gson gson;
    private Sql2oCourseDao courseDao;

    @BeforeClass
    public static void startServer() {
        String[] args = {PORT, TEST_DATASOURCE};
        Api.main(args);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        courseDao = new Sql2oCourseDao(sql2o);
        conn = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCoursesReturnsCreatedStatus() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http://test.com");

        ApiResponse res = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() throws Exception {
        Course course = newTestCourse();
        courseDao.add(course);

        ApiResponse res = client.request("GET", "/courses/" + course.getId());
        Course retrieved = gson.fromJson(res.getBody(), Course.class);

        assertEquals(course, retrieved);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() throws Exception {
        ApiResponse res = client.request("GET", "/courses/42");
        assertEquals(404, res.getStatus());
    }

    @Test
    public void addingReviewGivesCreatedStatus() throws Exception {
        Course course = newTestCourse();
        courseDao.add(course);
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 10);
        values.put("comment", "Test comment");

        ApiResponse res = client.request("POST",
                String.format("/courses/%d/reviews", course.getId()),
                gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");
    }
}