package com.xabe.jpa.hibernate.logger;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@Plugin(name = "ExtendedWhitespaceThrowablePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"xwEx", "xwThrowable", "xwException"})
public class ExtendedWhitespaceThrowablePatternConverter extends ThrowablePatternConverter {

  private final ExtendedThrowablePatternConverter delegate;

  private ExtendedWhitespaceThrowablePatternConverter(final Configuration configuration, final String[] options) {
    super("WhitespaceExtendedThrowable", "throwable", options, configuration);
    this.delegate = ExtendedThrowablePatternConverter.newInstance(configuration, options);
  }

  @Override
  public void format(final LogEvent event, final StringBuilder buffer) {
    if (event.getThrown() != null) {
      buffer.append(this.options.getSeparator());
      this.delegate.format(event, buffer);
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
  public static ExtendedWhitespaceThrowablePatternConverter newInstance(final Configuration configuration,
      final String[] options) {
    return new ExtendedWhitespaceThrowablePatternConverter(configuration, options);
  }
}
