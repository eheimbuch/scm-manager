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
import { binder, extensionPoints } from "@scm-manager/ui-extensions";
import { NavLink } from "../navigation";
import { Route } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Link, Links, Repository } from "@scm-manager/ui-types";

type GlobalRouteProps = {
  url: string;
  links: Links;
};

type RepositoryRouteProps = {
  url: string;
  repository: Repository;
};

type RepositoryNavProps = { url: string };

class ConfigurationBinder {
  i18nNamespace = "plugins";

  navLink(to: string, labelI18nKey: string, t: any) {
    return <NavLink to={to} label={t(labelI18nKey)} />;
  }

  route(path: string, Component: any) {
    return (
      <Route path={path} exact>
        {Component}
      </Route>
    );
  }

  bindGlobal(to: string, labelI18nKey: string, linkName: string, ConfigurationComponent: any) {
    // create predicate based on the link name of the index resource
    // if the linkname is not available, the navigation link and the route are not bound to the extension points
    const configPredicate = (props: extensionPoints.AdminSetting["props"]) => {
      return props.links && props.links[linkName];
    };

    // create NavigationLink with translated label
    const ConfigNavLink = () => {
      const [t] = useTranslation(this.i18nNamespace);
      return this.navLink("/admin/settings" + to, labelI18nKey, t);
    };

    // bind navigation link to extension point
    binder.bind<extensionPoints.AdminSetting>("admin.setting", ConfigNavLink, configPredicate, labelI18nKey);

    // route for global configuration, passes the link from the index resource to component
    const ConfigRoute: FC<GlobalRouteProps> = ({ url, links, ...additionalProps }) => {
      const link = links[linkName];
      if (link) {
        return this.route(
          url + "/settings" + to,
          <ConfigurationComponent link={(link as Link).href} {...additionalProps} />
        );
      }
      return null;
    };

    // bind config route to extension point
    binder.bind<extensionPoints.AdminRoute>("admin.route", ConfigRoute, configPredicate);
  }

  bindRepository(to: string, labelI18nKey: string, linkName: string, RepositoryComponent: any) {
    // create predicate based on the link name of the current repository route
    // if the linkname is not available, the navigation link and the route are not bound to the extension points
    const repoPredicate = (props: extensionPoints.RepositoryRoute["props"]) => {
      return props.repository && props.repository._links && props.repository._links[linkName];
    };

    // create NavigationLink with translated label
    const RepoNavLink: FC<RepositoryNavProps> = ({ url }) => {
      const [t] = useTranslation(this.i18nNamespace);
      return this.navLink(url + to, labelI18nKey, t);
    };

    // bind navigation link to extension point
    binder.bind<extensionPoints.RepositoryNavigation>("repository.navigation", RepoNavLink, repoPredicate);

    // route for global configuration, passes the current repository to component
    const RepoRoute: FC<RepositoryRouteProps> = ({ url, repository, ...additionalProps }) => {
      const link = repository._links[linkName];
      if (link) {
        return this.route(
          url + to,
          <RepositoryComponent repository={repository} link={(link as Link).href} {...additionalProps} />
        );
      }
      return null;
    };

    // bind config route to extension point
    binder.bind<extensionPoints.RepositoryRoute>("repository.route", RepoRoute, repoPredicate);
  }

  bindRepositorySetting(to: string, labelI18nKey: string, linkName: string, RepositoryComponent: any) {
    // create predicate based on the link name of the current repository route
    // if the linkname is not available, the navigation link and the route are not bound to the extension points
    const repoPredicate = (props: extensionPoints.RepositoryRoute["props"]) => {
      return props.repository && props.repository._links && props.repository._links[linkName];
    };

    // create NavigationLink with translated label
    const RepoNavLink: FC<RepositoryNavProps> = ({ url }) => {
      const [t] = useTranslation(this.i18nNamespace);
      return this.navLink(url + "/settings" + to, labelI18nKey, t);
    };

    // bind navigation link to extension point
    binder.bind<extensionPoints.RepositorySetting>("repository.setting", RepoNavLink, repoPredicate);

    // route for global configuration, passes the current repository to component
    const RepoRoute: FC<RepositoryRouteProps> = ({ url, repository, ...additionalProps }) => {
      const link = repository._links[linkName];
      if (link) {
        return this.route(
          url + "/settings" + to,
          <RepositoryComponent repository={repository} link={(link as Link).href} {...additionalProps} />
        );
      }
      return null;
    };

    // bind config route to extension point
    binder.bind<extensionPoints.RepositoryRoute>("repository.route", RepoRoute, repoPredicate);
  }
}

export default new ConfigurationBinder();
