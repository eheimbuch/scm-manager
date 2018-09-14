package sonia.scm.web;

import com.google.inject.Inject;
import sonia.scm.repository.spi.ScmProviderHttpServlet;
import sonia.scm.repository.spi.ScmProviderHttpServletProvider;

import javax.inject.Provider;

public class ScmGitServletProvider extends ScmProviderHttpServletProvider {

  @Inject
  private Provider<ScmGitServlet> servletProvider;

  public ScmGitServletProvider() {
    super("git");
  }

  @Override
  protected ScmProviderHttpServlet getRootServlet() {
    return servletProvider.get();
  }
}
