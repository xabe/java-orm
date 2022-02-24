package com.xabe.jpa.hibernate.logger;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@Plugin(name = "WhitespaceThrowablePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"wEx", "wThrowable", "wException"})
public class WhitespaceThrowablePatternConverter extends ThrowablePatternConverter {

  private WhitespaceThrowablePatternConverter(final Configuration configuration, final String[] options) {
    super("WhitespaceThrowable", "throwable", options, configuration);
  }

  @Override
  public void format(final LogEvent event, final StringBuilder buffer) {
    if (event.getThrown() != null) {
      buffer.append(this.options.getSeparator());
      super.format(event, buffer);
      buffer.append(this.options.getSeparator());
    }
  }

  /**
   * Creates a new instance of the class. Required by Log4J2.
   *
   * @param configuration current configuration
   * @param options pattern options, may be null. If first element is "short", only the first line of the throwable will be formatted.
   * @return a new {@code WhitespaceThrowablePatternConverter}
   */
  public static WhitespaceThrowablePatternConverter newInstance(final Configuration configuration, final String[] options) {
    return new WhitespaceThrowablePatternConverter(configuration, options);
  }


}
