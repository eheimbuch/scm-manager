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

package sonia.scm.cli;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import picocli.CommandLine;

public class CommandFactory implements CommandLine.IFactory {

  private final Injector injector;

  public CommandFactory(Injector injector, CliContext context) {
    this.injector = injector.createChildInjector(new CliContextModule(context));
  }

  @Override
  public <K> K create(Class<K> cls) throws Exception {
    return injector.getInstance(cls);
  }

  static class CliContextModule extends AbstractModule {

    private final CliContext context;

    private CliContextModule(CliContext context) {
      this.context = context;
    }

    @Override
    protected void configure() {
      bind(CliContext.class).toInstance(context);
    }
  }
}
