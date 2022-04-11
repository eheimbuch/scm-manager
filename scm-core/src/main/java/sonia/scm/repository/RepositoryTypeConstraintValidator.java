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

import sonia.scm.Type;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Collectors;

/**
 * Validator for {@link RepositoryTypeConstraint}.
 *
 * @since 2.33.0
 */
public class RepositoryTypeConstraintValidator implements ConstraintValidator<RepositoryTypeConstraint, String> {

  private final RepositoryManager repositoryManager;

  @Inject
  public RepositoryTypeConstraintValidator(RepositoryManager repositoryManager) {
    this.repositoryManager = repositoryManager;
  }

  public RepositoryManager getRepositoryManager() {
    return repositoryManager;
  }

  @Override
  public boolean isValid(String type, ConstraintValidatorContext context) {
    if (!isSupportedType(type)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(createMessage(context)).addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean isSupportedType(String type) {
    return repositoryManager.getConfiguredTypes()
      .stream().anyMatch(t -> t.getName().equalsIgnoreCase(type));
  }

  private String createMessage(ConstraintValidatorContext context) {
    String message = context.getDefaultConstraintMessageTemplate();
    return message + " " + commaSeparatedTypes();
  }

  private String commaSeparatedTypes() {
    return repositoryManager.getConfiguredTypes()
      .stream()
      .map(Type::getName)
      .collect(Collectors.joining(", "));
  }
}