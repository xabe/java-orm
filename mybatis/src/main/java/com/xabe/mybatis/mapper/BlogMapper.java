package com.xabe.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BlogMapper {

  void dropTable(@Param("name") String name);

  void createTableIfNotExist(@Param("name") String name);

  int insertBlog(@Param("blog") Blog blog);

  List<Blog> findBlogs();

  Blog getBlog(@Param("id") int id);

  int deleteBlog(@Param("id") int id);

  int updateBlog(@Param("blog") Blog blog);
}
