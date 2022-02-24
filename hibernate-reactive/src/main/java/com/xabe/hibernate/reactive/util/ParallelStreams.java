package com.xabe.hibernate.reactive.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ParallelStreams {

  public static <T> CompletableFuture<List<T>> allOfOrException(final Collection<CompletableFuture<T>> futures,
      final ExecutorService executorService) {
    final CompletableFuture<List<T>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenApplyAsync(items -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()), executorService);

    for (final CompletableFuture<?> f : futures) {
      f.handle((c, ex) -> ex == null || result.completeExceptionally(ex));
    }
    return result;
  }

}
