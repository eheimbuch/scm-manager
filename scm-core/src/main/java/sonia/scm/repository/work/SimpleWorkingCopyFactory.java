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

package sonia.scm.repository.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.RepositoryProvider;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * This class is responsible to govern the creation, the reuse and the destruction
 * of working copies. For every repository type there has to be an implementation
 * of this class to provide the repository specific logic to create, initialize,
 * reclaim and clean up working copies. To do this, the following methods have to be
 * implemented:
 *
 * <dl>
 *   <dt>{@link #getInitializer(C, File, String)}</dt>
 *   <dd>Creates a new clone of the repository for the given context in the given
 *     directory with the given branch checked out (if branches are supported).</dd>
 *   <dt>{@link #getReclaimer(C, File, String)}</dt>
 *   <dd>Reclaim the working directory with a already checked out clone of the
 *     repository given in the context, so that the directory is not modified in
 *     respect to the repository and the given branch is checked out (if branches
 *     are supported).</dd>
 *   <dt>{@link #closeWorkingCopy(W)}</dt>
 *   <dd>Closes resources allocated for the working copy, so that the directory can
 *     be put to the cache. Will be called at the end of the operation.</dd>
 *   <dt>{@link #closeRepository(R)}</dt>
 *   <dd>Closes resources allocated for the central repository.</dd>
 * </dl>
 * <pre>
 *                     ┌─────────────┐          ┌───────────────────────────┐                                                      ┌───────────────┐          ┌───────────────┐
 *                     │ModifyCommand│          │SimpleGitWorkingCopyFactory│                                                      │WorkingCopyPool│          │WorkdirProvider│
 *                     └──────┬──────┘          └─────────────┬─────────────┘                                                      └───────┬───────┘          └───────┬───────┘
 *                            │      createWorkingCopy        │                                                                            │                          │
 *                            │──────────────────────────────>│                                                                            │                          │
 *                            │                               │                                                                            │                          │
 *                            │                               ────┐                                                                        │                          │
 *                            │                                   │ createContext                                                          │                          │
 *                            │                               <───┘                                                                        │                          │
 *                            │                               │                                                                            │                          │
 *                            │                               │        create          ┌─────────┐                                         │                          │
 *                            │                               │──────────────────────> │Reclaimer│                                         │                          │
 *                            │                               │                        └────┬────┘                                         │                          │
 *                            │                               │                  create     │               ┌───────────┐                  │                          │
 *                            │                               │───────────────────────────────────────────> │Initializer│                  │                          │
 *                            │                               │                             │               └─────┬─────┘                  │                          │
 *                            │                               │                             │getWorkingCopy       │                        │                          │
 *                            │                               │───────────────────────────────────────────────────────────────────────────>│                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                   reclaim                    │                          │
 *                            │                               │                             │<─────────────────────────────────────────────│                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │       ╔══════╤════════════════╪═════════════════════════════╪═════════════════════╪════════════════════════╪══════════════════════════╪═════════════════╗
 *                            │       ║ ALT  │  reclaim successful                          │                     │                        │                          │                 ║
 *                            │       ╟──────┘                │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │─────────────────────────────────────────────>│                          │                 ║
 *                            │       ╠═══════════════════════╪═════════════════════════════╪═════════════════════╪════════════════════════╪══════════════════════════╪═════════════════╣
 *                            │       ║ [reclaim fails; create new]                         │                     │                        │                          │                 ║
 *                            │       ║                       │                          ReclaimFailedException   │                        │                          │                 ║
 *                            │       ║                       │───────────────────────────────────────────────────────────────────────────>│                          │                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │                        │    createNewWorkdir      │                 ║
 *                            │       ║                       │                             │                     │                        │─────────────────────────>│                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │                        │<─────────────────────────│                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │      initialize        │                          │                 ║
 *                            │       ║                       │                             │                     │<───────────────────────│                          │                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │                        │                          │                 ║
 *                            │       ║                       │                             │                     │───────────────────────>│                          │                 ║
 *                            │       ╚═══════════════════════╪═════════════════════════════╪═════════════════════╪════════════════════════╪══════════════════════════╪═════════════════╝
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │ParentAndClone       │                        │                          │
 *                            │                               │<───────────────────────────────────────────────────────────────────────────│                          │
 *                            │                               │                             │                     │                        │                          │
 *                            │                               │                             │                     │                        │                          │                  ┌───────────┐
 *                            │                               │────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────> │WorkingCopy│
 *                            │                               │                             │                     │                        │                          │                  └─────┬─────┘
 *                            │         WorkingCopy           │                             │                     │                        │                          │                        │
 *                            │<──────────────────────────────│                             │                     │                        │                          │                        │
 *                            │                               │                             │                     │                        │                          │                        │
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            │                               │                             │              doWork │                        │                          │                        │
 *                            │───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────>│
 *                            │                               │                             │                     │                        │                          │                        │
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            .                               .                             .                     .                        .                          .                        .
 *                            │                               │                             │               close │                        │                          │                        │
 *                            │───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────>│
 *                            │                               │                             │                     │                        │                          │                        │
 *                            │                               │                             │                     │         close          │                          │                        │
 *                            │                               │<───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────│
 *                            │                               │                             │                     │                        │                          │                        │
 *                            │                               ────┐                         │                     │                        │                          │                        │
 *                            │                                   │ closeWorkingCopy        │                     │                        │                          │                        │
 *                            │                               <───┘                         │                     │                        │                          │                        │
 *                            │                               │                             │                     │                        │                          │                        │
 *                            │                               ────┐                         │                     │                        │                          │                        │
 *                            │                                   │ closeRepository         │                     │                        │                          │                        │
 *                            │                               <───┘                         │                     │                        │                          │                        │
 *                            │                               │                             │                     │                        │                          │                        │
 *                            │                               │                             │ contextClosed       │                        │                          │                        │
 *                            │                               │───────────────────────────────────────────────────────────────────────────>│                          │                        │
 * </pre>
 * <img src="http://www.plantuml.com/plantuml/png/fPF1QiCm38RlVWgV0-a3U10wmZfss2ZTO6TZgqLCiOBjhCsUVeIcmKkPcx5RB7t_7qcsrY5g7xk6n3DRtFSrDursjgnUiMa97Z6-i_z0TwYvxJVQHFQzLD9uq16IbCZmMJDrjghPHJZ5l8tSWI6D3VYY67Kt14yE8sh2hyMI9BPb9dM051C7prqhttml8qj_BaVCD6KrrQakYAPumMNeZ84GzXs92IohIivi1520IRJNIE5k7BnqSCotSPgxgV5N6uq4zk5ae8t8xhhs8M3HRpr_eWK_3kq5ZcD2p82oci_isZEv1eNJjvZ2l_JlxBLLzYrjjGSNxvsjYTtoYowAF5hzs0sL5YfMgzLyTPAqiZoSRb66E43IQtvbHZn3B90dyNywxQ3bWCFvRvjbpYjV3nvMhC7Phg5l"/>
 * @param <R> Type of central repository location
 * @param <W> Type of working copy for repository
 * @param <C> Type of repository context
 */
/*
http://www.plantuml.com/plantuml/uml/fPF1QiCm38RlVWgV0-a3U10wmZfss2ZTO6TZgqLCiOBjhCsUVeIcmKkPcx5RB7t_7qcsrY5g7xk6n3DRtFSrDursjgnUiMa97Z6-i_z0TwYvxJVQHFQzLD9uq16IbCZmMJDrjghPHJZ5l8tSWI6D3VYY67Kt14yE8sh2hyMI9BPb9dM051C7prqhttml8qj_BaVCD6KrrQakYAPumMNeZ84GzXs92IohIivi1520IRJNIE5k7BnqSCotSPgxgV5N6uq4zk5ae8t8xhhs8M3HRpr_eWK_3kq5ZcD2p82oci_isZEv1eNJjvZ2l_JlxBLLzYrjjGSNxvsjYTtoYowAF5hzs0sL5YfMgzLyTPAqiZoSRb66E43IQtvbHZn3B90dyNywxQ3bWCFvRvjbpYjV3nvMhC7Phg5l
@startuml
ModifyCommand->SimpleGitWorkingCopyFactory : createWorkingCopy
SimpleGitWorkingCopyFactory->SimpleGitWorkingCopyFactory:createContext
SimpleGitWorkingCopyFactory-> Reclaimer**:create
SimpleGitWorkingCopyFactory-> Initializer**:create
SimpleGitWorkingCopyFactory->WorkingCopyPool:getWorkingCopy
group Try to reclaim
WorkingCopyPool->Reclaimer:reclaim
alt reclaim successful
Reclaimer->> WorkingCopyPool
else reclaim fails; create new
SimpleGitWorkingCopyFactory->x WorkingCopyPool:ReclaimFailedException
WorkingCopyPool->WorkdirProvider:createNewWorkdir
WorkdirProvider->>WorkingCopyPool
WorkingCopyPool->Initializer:initialize
Initializer->> WorkingCopyPool
end
WorkingCopyPool->>SimpleGitWorkingCopyFactory:ParentAndClone
SimpleGitWorkingCopyFactory->WorkingCopy**
SimpleGitWorkingCopyFactory->>ModifyCommand: WorkingCopy
...
ModifyCommand->WorkingCopy:doWork
...
ModifyCommand->WorkingCopy:close
WorkingCopy->SimpleGitWorkingCopyFactory:close
SimpleGitWorkingCopyFactory->SimpleGitWorkingCopyFactory:closeWorkingCopy
SimpleGitWorkingCopyFactory->SimpleGitWorkingCopyFactory:closeRepository
SimpleGitWorkingCopyFactory->WorkingCopyPool:contextClosed
@enduml
*/
public abstract class SimpleWorkingCopyFactory<R, W, C extends RepositoryProvider> implements WorkingCopyFactory<R, W, C>, ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleWorkingCopyFactory.class);

  private final WorkingCopyPool workingCopyPool;

  public SimpleWorkingCopyFactory(WorkingCopyPool workingCopyPool) {
    this.workingCopyPool = workingCopyPool;
  }

  @Override
  public WorkingCopy<R, W> createWorkingCopy(C repositoryContext, String initialBranch) {
    try {
      WorkingCopyContext<R, W, C> workingCopyContext = createWorkingCopyContext(repositoryContext, initialBranch);
      WorkingCopyPool.ParentAndClone<R, W> parentAndClone = workingCopyPool.getWorkingCopy(workingCopyContext);
      return new WorkingCopy<>(parentAndClone.getClone(), parentAndClone.getParent(), () -> this.close(workingCopyContext, parentAndClone), parentAndClone.getDirectory());
    } catch (WorkingCopyFailedException e) {
      throw new InternalRepositoryException(repositoryContext.get(), "could not create working copy for repository in temporary directory", e);
    }
  }

  private WorkingCopyContext<R, W, C> createWorkingCopyContext(C repositoryContext, String initialBranch) {
    return new WorkingCopyContext<>(
      initialBranch,
      repositoryContext,
      getInitializer(repositoryContext),
      getReclaimer(repositoryContext)
    );
  }

  private void close(WorkingCopyContext<R, W, C> workingCopyContext, WorkingCopyPool.ParentAndClone<R, W> parentAndClone) {
    try {
      closeWorkingCopy(parentAndClone.getClone());
    } catch (Exception e) {
      LOG.warn("could not close clone for {} in directory {}", workingCopyContext.getScmRepository(), parentAndClone.getDirectory(), e);
    }
    try {
      closeRepository(parentAndClone.getParent());
    } catch (Exception e) {
      LOG.warn("could not close central repository for {}", workingCopyContext.getScmRepository(), e);
    }
    try {
      workingCopyPool.contextClosed(workingCopyContext, parentAndClone.getDirectory());
    } catch (Exception e) {
      LOG.warn("could not close context for {} with directory {}", workingCopyContext.getScmRepository(), parentAndClone.getDirectory(), e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    workingCopyPool.shutdown();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // nothing to do
  }

  @FunctionalInterface
  public interface WorkingCopyInitializer<R, W> {
    WorkingCopyPool.ParentAndClone<R, W> initialize(File target, String initialBranch) throws WorkingCopyFailedException;
  }

  @FunctionalInterface
  public interface WorkingCopyReclaimer<R, W> {
    WorkingCopyPool.ParentAndClone<R, W> reclaim(File target, String initialBranch) throws ReclaimFailedException;
  }

  protected abstract WorkingCopyInitializer<R, W> getInitializer(C context);

  protected abstract WorkingCopyReclaimer<R, W> getReclaimer(C context);

  @SuppressWarnings("squid:S00112")
  // We do allow implementations to throw arbitrary exceptions here, so that we can handle them in closeCentral
  protected abstract void closeRepository(R repository) throws Exception;

  @SuppressWarnings("squid:S00112")
  // We do allow implementations to throw arbitrary exceptions here, so that we can handle them in closeWorkingCopy
  protected abstract void closeWorkingCopy(W workingCopy) throws Exception;

  public static class ReclaimFailedException extends Exception {
    public ReclaimFailedException(String message) {
      super(message);
    }

    public ReclaimFailedException(Throwable cause) {
      super(cause);
    }

    public ReclaimFailedException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
