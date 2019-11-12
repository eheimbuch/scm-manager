package sonia.scm.repository.spi;

import com.google.common.collect.ImmutableSet;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.ResolveMerger;
import sonia.scm.repository.GitWorkdirFactory;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.api.MergeCommandResult;
import sonia.scm.repository.api.MergeDryRunCommandResult;
import sonia.scm.repository.api.MergeStrategy;
import sonia.scm.repository.api.MergeStrategyNotSupportedException;

import java.io.IOException;
import java.util.Set;

import static org.eclipse.jgit.merge.MergeStrategy.RECURSIVE;

public class GitMergeCommand extends AbstractGitCommand implements MergeCommand {

  private final GitWorkdirFactory workdirFactory;

  private static final Set<MergeStrategy> STRATEGIES = ImmutableSet.of(
    MergeStrategy.MERGE_COMMIT,
    MergeStrategy.FAST_FORWARD_IF_POSSIBLE,
    MergeStrategy.SQUASH
  );

  GitMergeCommand(GitContext context, sonia.scm.repository.Repository repository, GitWorkdirFactory workdirFactory) {
    super(context, repository);
    this.workdirFactory = workdirFactory;
  }

  @Override
  public MergeCommandResult merge(MergeCommandRequest request) {
    return mergeWithStrategy(request);
  }

  private MergeCommandResult mergeWithStrategy(MergeCommandRequest request) {
    switch(request.getMergeStrategy()) {
      case SQUASH:
        return inClone(clone -> new GitMergeWithSquash(clone, request, context, repository), workdirFactory, request.getTargetBranch());

      case FAST_FORWARD_IF_POSSIBLE:
        return inClone(clone -> new GitFastForwardIfPossible(clone, request, context, repository), workdirFactory, request.getTargetBranch());

      case MERGE_COMMIT:
        return inClone(clone -> new GitMergeCommit(clone, request, context, repository), workdirFactory, request.getTargetBranch());

      default:
        throw new MergeStrategyNotSupportedException(repository, request.getMergeStrategy());
    }
  }

  @Override
  public MergeDryRunCommandResult dryRun(MergeCommandRequest request) {
    try {
      Repository repository = context.open();
      ResolveMerger merger = (ResolveMerger) RECURSIVE.newMerger(repository, true);
      return new MergeDryRunCommandResult(
        merger.merge(
          resolveRevisionOrThrowNotFound(repository, request.getBranchToMerge()),
          resolveRevisionOrThrowNotFound(repository, request.getTargetBranch())));
    } catch (IOException e) {
      throw new InternalRepositoryException(context.getRepository(), "could not clone repository for merge", e);
    }
  }

  @Override
  public boolean isSupported(MergeStrategy strategy) {
    return STRATEGIES.contains(strategy);
  }

  @Override
  public Set<MergeStrategy> getSupportedMergeStrategies() {
    return STRATEGIES;
  }

}
