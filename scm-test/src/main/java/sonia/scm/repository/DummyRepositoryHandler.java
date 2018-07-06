/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
 */


package sonia.scm.repository;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.Type;
import sonia.scm.io.DefaultFileSystem;
import sonia.scm.store.ConfigurationStoreFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

/**
 *
 * @author Sebastian Sdorra
 */
public class DummyRepositoryHandler
  extends AbstractSimpleRepositoryHandler<SimpleRepositoryConfig> {

  public static final String TYPE_DISPLAYNAME = "Dummy";

  public static final String TYPE_NAME = "dummy";

  public static final Type TYPE = new Type(TYPE_NAME, TYPE_DISPLAYNAME);

  private Set<String> existingRepoNames = new HashSet<>();

  public DummyRepositoryHandler(ConfigurationStoreFactory storeFactory) {
    super(storeFactory, new DefaultFileSystem());
  }

  @Override
  public Type getType() {
    return TYPE;
  }


  @Override
  protected void create(Repository repository, File directory)
    throws RepositoryException {
    if (existingRepoNames.contains(repository.getNamespace() + repository.getName())) {
      throw new RepositoryAlreadyExistsException("Repo exists");
    } else {
      existingRepoNames.add(repository.getNamespace() + repository.getName());
    }
  }

  @Override
  protected SimpleRepositoryConfig createInitialConfig() {
    return new SimpleRepositoryConfig();
  }

  @Override
  protected Class<SimpleRepositoryConfig> getConfigClass() {
    return SimpleRepositoryConfig.class;
  }
}
