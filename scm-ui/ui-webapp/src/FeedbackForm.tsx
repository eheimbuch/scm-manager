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

import { FC } from "react";
import { useSubject } from "@scm-manager/ui-api";

const loadFeedbackForm = () => {
  const id = "wMLGE74pg82Hrp4vslZWoS7MuPBrSnoIuDmOiSYK";
  const js = document.createElement("script");
  js.setAttribute("type", "text/javascript");
  js.setAttribute("src", "//deploy.mopinion.com/js/pastease.js");
  js.async = true;
  document.getElementsByTagName("head")[0].appendChild(js);
  const t = setInterval(function() {
    try {
      //@ts-ignore Cannot resolve Pastease
      Pastease.load(id);
      clearInterval(t);
    } catch (e) {}
  }, 50);
  return null;
};

export const FeedbackForm: FC = () => {
  const { isAuthenticated } = useSubject();

  if (!isAuthenticated) {
    return null;
  }
  return loadFeedbackForm();
};
