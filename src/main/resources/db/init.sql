CREATE TABLE IF NOT EXISTS courses (
  id   INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR,
  url  VARCHAR
);

CREATE TABLE IF NOT EXISTS reviews (
  id        INTEGER PRIMARY KEY AUTO_INCREMENT,
  course_id INTEGER,
  rating    INTEGER,
  comment   VARCHAR,
  FOREIGN KEY (course_id) REFERENCES public.courses (id)
);
