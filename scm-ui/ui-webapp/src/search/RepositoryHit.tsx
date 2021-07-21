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

import React, { FC } from "react";
import { useDateFieldValue, useStringFieldValue } from "./fields";
import { Repository } from "@scm-manager/ui-types";
import { Link } from "react-router-dom";
import { DateFromNow, RepositoryAvatar } from "@scm-manager/ui-components";
import TextField from "./TextField";
import Hit, { HitProps } from "./Hit";

const RepositoryHit: FC<HitProps> = ({ hit }) => {
  const namespace = useStringFieldValue(hit, "namespace");
  const name = useStringFieldValue(hit, "name");
  const type = useStringFieldValue(hit, "type");
  const lastModified = useDateFieldValue(hit, "lastModified");
  const creationDate = useDateFieldValue(hit, "creationDate");
  const date = lastModified || creationDate;

  if (!namespace || !name || !type) {
    return null;
  }

  const repository: Repository = {
    namespace,
    name,
    type,
    _links: {},
    _embedded: hit._embedded,
  };

  return (
    <Hit>
      <Hit.Left>
        <Link to={`/repo/${namespace}/${name}`}>
          <RepositoryAvatar repository={repository} size={48} />
        </Link>
      </Hit.Left>
      <Hit.Content>
        <Link to={`/repo/${namespace}/${name}`}>
          <Hit.Title>
            {namespace}/{name}
          </Hit.Title>
        </Link>
        <p>
          <TextField hit={hit} field="description" />
        </p>
      </Hit.Content>
      <Hit.Right>
        <DateFromNow date={date} />
      </Hit.Right>
    </Hit>
  );
};

export default RepositoryHit;
