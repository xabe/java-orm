package com.xabe.mybatis.config;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

public class HikariCPDataSourceFactory implements DataSourceFactory {

  private HikariDataSource hikariDataSource;

  @Override
  public void setProperties(final Properties properties) {
    this.hikariDataSource = new HikariDataSource(new HikariConfig(properties));
  }

  @Override
  public DataSource getDataSource() {
    return this.hikariDataSource;
  }
}
