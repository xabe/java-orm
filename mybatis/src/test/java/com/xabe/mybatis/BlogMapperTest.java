package com.xabe.mybatis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

import com.xabe.mybatis.mapper.Blog;
import com.xabe.mybatis.mapper.BlogMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BlogMapperTest {

  private int id;

  private static SqlSessionFactory factory = null;

  @BeforeAll
  public static void setup() throws IOException {
    final String resource = "mybatis-config.xml";
    final Reader reader = Resources.getResourceAsReader(resource);
    factory = new SqlSessionFactoryBuilder().build(reader);
    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      blogMapper.dropTable("blog");
      blogMapper.createTableIfNotExist("blog");
      session.commit();
    }
    reader.close();
  }

  @Test
  @Order(1)
  public void shouldCreate() throws Exception {
    //Given
    final Blog blog = Blog.builder().name("name").date(LocalDateTime.now()).build();

    //When
    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      this.id = blogMapper.insertBlog(blog);
      session.commit();
    }

    //Then
    assertThat(blog, is(notNullValue()));
    assertThat(this.id, is(notNullValue()));
    assertThat(blog.getId(), is(notNullValue()));
    assertThat(blog.getId(), is(this.id));
  }

  @Test
  @Order(2)
  public void shouldCreate2() throws Exception {
    //Given
    final Blog blog = Blog.builder().name("name").build();

    //When
    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      blogMapper.insertBlog(blog);
      session.commit();
    }

    //Then
    assertThat(blog, is(notNullValue()));
    assertThat(blog.getId(), is(notNullValue()));
  }

  @Test
  @Order(3)
  public void shouldGetBlogs() throws Exception {

    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      final List<Blog> blogs = blogMapper.findBlogs();

      assertThat(blogs, is(notNullValue()));
      assertThat(blogs, is(hasSize(2)));
    }
  }

  @Test
  @Order(4)
  public void shouldGetBlog() throws Exception {

    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      final Blog blog = blogMapper.getBlog(this.id);

      assertThat(blog, is(notNullValue()));
      assertThat(blog.getId(), is(notNullValue()));
      assertThat(blog.getName(), is(notNullValue()));
      assertThat(blog.getDate(), is(notNullValue()));
    }
  }

  @Test
  @Order(5)
  public void shouldUpdateBlog() throws Exception {
    final Blog blog = Blog.builder().name("nameUpdate").id(this.id).date(LocalDateTime.now()).build();

    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      final int result = blogMapper.updateBlog(blog);
      session.commit();
      final Blog blogUpdate = blogMapper.getBlog(this.id);

      assertThat(result, is(1));
      assertThat(blogUpdate.getId(), is(blog.getId()));
      assertThat(blogUpdate.getName(), is(blog.getName()));
      assertThat(blogUpdate.getDate(), is(notNullValue()));
    }
  }

  @Test
  @Order(6)
  public void shouldDeleteBlog() throws Exception {

    try (final SqlSession session = factory.openSession()) {
      final BlogMapper blogMapper = session.getMapper(BlogMapper.class);
      final int result = blogMapper.deleteBlog(this.id);
      session.commit();
      final Blog blog = blogMapper.getBlog(this.id);

      assertThat(result, is(1));
      assertThat(blog, is(nullValue()));
    }
  }


}
