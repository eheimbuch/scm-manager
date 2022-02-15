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

import React, { FC, useEffect, useState } from "react";
import { Button, ErrorNotification, Loading, Modal } from "@scm-manager/ui-components";
import styled from "styled-components";

declare const Pastease: { load: (id: string) => void };

const useFeedbackForm = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<Error | null>();

  useEffect(() => {
    const id = "wMLGE74pg82Hrp4vslZWoS7MuPBrSnoIuDmOiSYK";
    const js = document.createElement("script");
    js.setAttribute("type", "text/javascript");
    js.setAttribute("src", "//deploy.mopinion.com/js/pastease.js");
    js.setAttribute("id", "pasteaseLoaderScript");
    js.onerror = () => setError(new Error("Failed to load feedback form"));
    js.async = true;
    js.onload = () => {
      setIsLoading(false);
      Pastease.load(id);
    };
    document.getElementsByTagName("head")[0].appendChild(js);
    return () => {
      const pasteaseLoader = document.getElementById("pasteaseLoaderScript");
      if (pasteaseLoader) {
        document.getElementsByTagName("head")[0].removeChild(pasteaseLoader);
      }

      const mopinionScript = document.getElementById("mopinionFeedbackScript");
      if (mopinionScript) {
        document.getElementsByTagName("head")[0].removeChild(mopinionScript);
      }
    };
  }, []);

  return {
    isLoading,
    error,
    id: "surveyContent"
  };
};

export const Feedback: FC = () => {
  const [showModal, setShowModal] = useState(false);

  if (showModal) {
    return <FeedbackForm close={() => setShowModal(false)} />;
  }

  return <FeedbackTriggerButton openModal={() => setShowModal(true)} />;
};

const TriggerButton = styled(Button)`
  position: fixed;
  z-index: 9999999;
  right: 1rem;
  bottom: -1px;
  border-radius: 0.2rem 0.2rem 0 0;
`;

const FeedbackTriggerButton: FC<{ openModal: () => void }> = ({ openModal }) => {
  return <TriggerButton action={openModal} color="info" label="Feedback" icon="comment" />;
};

const FeedbackForm: FC<{ close: () => void }> = ({ close }) => {
  const { isLoading, error, id } = useFeedbackForm();
  return (
    <Modal title={"Leave us some feedback"} active={true} closeFunction={close}>
      {isLoading ? <Loading /> : null}
      {error ? <ErrorNotification error={error} /> : null}
      <div id={id} />
    </Modal>
  );
};
