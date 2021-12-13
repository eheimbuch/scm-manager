/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sonia.scm.repository;

import com.github.legman.Subscribe;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.search.Id;
import sonia.scm.search.Index;
import sonia.scm.search.IndexLog;
import sonia.scm.search.IndexLogStore;
import sonia.scm.search.IndexTask;
import sonia.scm.search.SearchEngine;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Optional;

@Extension
public class ChangesetIndexer implements ServletContextListener {

  private final SearchEngine searchEngine;

  @Inject
  public ChangesetIndexer(SearchEngine searchEngine) {
    this.searchEngine = searchEngine;
  }

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    searchEngine.forType(Changeset.class).update(ReIndexAll.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // Do nothing
  }

  @Subscribe(async = false)
  public void handleEvent(PostReceiveRepositoryHookEvent event) {
    searchEngine.forType(Changeset.class).update(ReIndexAll.class);
  }

  private void handleEvent(Repository repository, Changeset changeset) {
    searchEngine.forType(Changeset.class).update(index -> store(index, repository, changeset));
  }

  private static void store(Index<Changeset> index, Repository repository, Changeset changeset) {
    index.store(
      id(repository, changeset),
      RepositoryPermissions.read(repository).asShiroString(),
      changeset
    );
  }

  private static Id<Changeset> id(Repository repository, Changeset changeset) {
    return Id.of(Changeset.class, changeset).and(repository);
  }

  private static class ReIndexAll implements IndexTask<Changeset> {

    private final RepositoryServiceFactory repositoryServiceFactory;
    private final RepositoryManager repositoryManager;
    private final IndexLogStore logStore;

    @Inject
    private ReIndexAll(RepositoryServiceFactory repositoryServiceFactory, RepositoryManager repositoryManager, IndexLogStore logStore) {
      this.repositoryServiceFactory = repositoryServiceFactory;
      this.repositoryManager = repositoryManager;
      this.logStore = logStore;
    }

    @Override
    public void update(Index<Changeset> index) {
      Optional<IndexLog> indexLog = logStore.defaultIndex().get(Changeset.class);
      if (!indexLog.isPresent() || indexLog.get().getVersion() != Changeset.VERSION) {
        try {
          reindexAll(index);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    private void reindexAll(Index<Changeset> index) throws IOException {
      index.delete().all();
      for (Repository repo : repositoryManager.getAll()) {
        reindexRepository(index, repo);
      }
    }

    private void reindexRepository(Index<Changeset> index, Repository repository) throws IOException {
      try (RepositoryService repositoryService = repositoryServiceFactory.create(repository)) {
        repositoryService.getLogCommand().getChangesets().getChangesets().forEach(changeset -> store(index, repository, changeset));
      }
    }
  }

}
