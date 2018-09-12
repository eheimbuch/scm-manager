package sonia.scm.api.v2.resources;

import sonia.scm.repository.NamespaceAndName;

import javax.inject.Inject;
import java.net.URI;

class ResourceLinks {

  private final UriInfoStore uriInfoStore;

  @Inject
  ResourceLinks(UriInfoStore uriInfoStore) {
    this.uriInfoStore = uriInfoStore;
  }


  GroupLinks group() {
    return new GroupLinks(uriInfoStore.get());
  }

  static class GroupLinks {
    private final LinkBuilder groupLinkBuilder;

    GroupLinks(ScmPathInfo uriInfo) {
      groupLinkBuilder = new LinkBuilder(uriInfo, GroupRootResource.class, GroupResource.class);
    }

    String self(String name) {
      return groupLinkBuilder.method("getGroupResource").parameters(name).method("get").parameters().href();
    }

    String delete(String name) {
      return groupLinkBuilder.method("getGroupResource").parameters(name).method("delete").parameters().href();
    }

    String update(String name) {
      return groupLinkBuilder.method("getGroupResource").parameters(name).method("update").parameters().href();
    }
  }

  GroupCollectionLinks groupCollection() {
    return new GroupCollectionLinks(uriInfoStore.get());
  }

  static class GroupCollectionLinks {
    private final LinkBuilder collectionLinkBuilder;

    GroupCollectionLinks(ScmPathInfo uriInfo) {
      collectionLinkBuilder = new LinkBuilder(uriInfo, GroupRootResource.class, GroupCollectionResource.class);
    }

    String self() {
      return collectionLinkBuilder.method("getGroupCollectionResource").parameters().method("getAll").parameters().href();
    }

    String create() {
      return collectionLinkBuilder.method("getGroupCollectionResource").parameters().method("create").parameters().href();
    }
  }

  UserLinks user() {
    return new UserLinks(uriInfoStore.get());
  }

  static class UserLinks {
    private final LinkBuilder userLinkBuilder;

    UserLinks(ScmPathInfo uriInfo) {
      userLinkBuilder = new LinkBuilder(uriInfo, UserRootResource.class, UserResource.class);
    }

    String self(String name) {
      return userLinkBuilder.method("getUserResource").parameters(name).method("get").parameters().href();
    }

    String delete(String name) {
      return userLinkBuilder.method("getUserResource").parameters(name).method("delete").parameters().href();
    }

    String update(String name) {
      return userLinkBuilder.method("getUserResource").parameters(name).method("update").parameters().href();
    }
  }

  UserCollectionLinks userCollection() {
    return new UserCollectionLinks(uriInfoStore.get());
  }

  static class UserCollectionLinks {
    private final LinkBuilder collectionLinkBuilder;

    UserCollectionLinks(ScmPathInfo uriInfo) {
      collectionLinkBuilder = new LinkBuilder(uriInfo, UserRootResource.class, UserCollectionResource.class);
    }

    String self() {
      return collectionLinkBuilder.method("getUserCollectionResource").parameters().method("getAll").parameters().href();
    }

    String create() {
      return collectionLinkBuilder.method("getUserCollectionResource").parameters().method("create").parameters().href();
    }
  }

  ConfigLinks config() {
    return new ConfigLinks(uriInfoStore.get());
  }

  static class ConfigLinks {
    private final LinkBuilder configLinkBuilder;

    ConfigLinks(ScmPathInfo uriInfo) {
      configLinkBuilder = new LinkBuilder(uriInfo, ConfigResource.class);
    }

    String self() {
      return configLinkBuilder.method("get").parameters().href();
    }

    String update() {
      return configLinkBuilder.method("update").parameters().href();
    }
  }

  public RepositoryLinks repository() {
    return new RepositoryLinks(uriInfoStore.get());
  }

  static class RepositoryLinks {
    private final LinkBuilder repositoryLinkBuilder;
    private final ScmPathInfo uriInfo;

    RepositoryLinks(ScmPathInfo uriInfo) {
      repositoryLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class);
      this.uriInfo = uriInfo;
    }

    String self(String namespace, String name) {
      return repositoryLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("get").parameters().href();
    }

    String delete(String namespace, String name) {
      return repositoryLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("delete").parameters().href();
    }

    String update(String namespace, String name) {
      return repositoryLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("update").parameters().href();
    }
  }

  RepositoryCollectionLinks repositoryCollection() {
    return new RepositoryCollectionLinks(uriInfoStore.get());
  }

  static class RepositoryCollectionLinks {
    private final LinkBuilder collectionLinkBuilder;

    RepositoryCollectionLinks(ScmPathInfo uriInfo) {
      collectionLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryCollectionResource.class);
    }

    String self() {
      return collectionLinkBuilder.method("getRepositoryCollectionResource").parameters().method("getAll").parameters().href();
    }

    String create() {
      return collectionLinkBuilder.method("getRepositoryCollectionResource").parameters().method("create").parameters().href();
    }
  }

  public RepositoryTypeLinks repositoryType() {
    return new RepositoryTypeLinks(uriInfoStore.get());
  }

  static class RepositoryTypeLinks {
    private final LinkBuilder repositoryTypeLinkBuilder;

    RepositoryTypeLinks(ScmPathInfo uriInfo) {
      repositoryTypeLinkBuilder = new LinkBuilder(uriInfo, RepositoryTypeRootResource.class, RepositoryTypeResource.class);
    }

    String self(String name) {
      return repositoryTypeLinkBuilder.method("getRepositoryTypeResource").parameters(name).method("get").parameters().href();
    }
  }

  public RepositoryTypeCollectionLinks repositoryTypeCollection() {
    return new RepositoryTypeCollectionLinks(uriInfoStore.get());
  }

  static class RepositoryTypeCollectionLinks {
    private final LinkBuilder collectionLinkBuilder;

    RepositoryTypeCollectionLinks(ScmPathInfo uriInfo) {
      collectionLinkBuilder = new LinkBuilder(uriInfo, RepositoryTypeRootResource.class, RepositoryTypeCollectionResource.class);
    }

    String self() {
      return collectionLinkBuilder.method("getRepositoryTypeCollectionResource").parameters().method("getAll").parameters().href();
    }
  }


  public TagCollectionLinks tag() {
    return new TagCollectionLinks(uriInfoStore.get());
  }

  static class TagCollectionLinks {
    private final LinkBuilder tagLinkBuilder;

    TagCollectionLinks(ScmPathInfo uriInfo) {
      tagLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, TagRootResource.class);
    }

    String self(String namespace, String name, String tagName) {
      return tagLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("tags").parameters().method("get").parameters(tagName).href();
    }

    String all(String namespace, String name) {
      return tagLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("tags").parameters().method("getAll").parameters().href();
    }
  }

  public DiffLinks diff() {
    return new DiffLinks(uriInfoStore.get());
  }

  static class DiffLinks {
    private final LinkBuilder diffLinkBuilder;

    DiffLinks(ScmPathInfo uriInfo) {
      diffLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, DiffRootResource.class);
    }

    String self(String namespace, String name, String id) {
      return diffLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("diff").parameters().method("get").parameters(id).href();
    }

    String all(String namespace, String name) {
      return diffLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("diff").parameters().method("getAll").parameters().href();
    }
  }

  public BranchLinks branch() {
    return new BranchLinks(uriInfoStore.get());
  }

  static class BranchLinks {
    private final LinkBuilder branchLinkBuilder;

    BranchLinks(ScmPathInfo uriInfo) {
      branchLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, BranchRootResource.class);
    }

    String self(NamespaceAndName namespaceAndName, String branch) {
      return branchLinkBuilder.method("getRepositoryResource").parameters(namespaceAndName.getNamespace(), namespaceAndName.getName()).method("branches").parameters().method("get").parameters(branch).href();
    }

    public String history(NamespaceAndName namespaceAndName, String branch) {
      return branchLinkBuilder.method("getRepositoryResource").parameters(namespaceAndName.getNamespace(), namespaceAndName.getName()).method("branches").parameters().method("history").parameters(branch).href();
    }
  }

  public BranchCollectionLinks branchCollection() {
    return new BranchCollectionLinks(uriInfoStore.get());
  }

  static class BranchCollectionLinks {
    private final LinkBuilder branchLinkBuilder;

    BranchCollectionLinks(ScmPathInfo uriInfo) {
      branchLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, BranchRootResource.class);
    }

    String self(String namespace, String name) {
      return branchLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("branches").parameters().method("getAll").parameters().href();
    }
  }

  public ChangesetLinks changeset() {
    return new ChangesetLinks(uriInfoStore.get());
  }

  static class ChangesetLinks {
    private final LinkBuilder changesetLinkBuilder;

    ChangesetLinks(ScmPathInfo uriInfo) {
      changesetLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, ChangesetRootResource.class);
    }

    String self(String namespace, String name, String changesetId) {
      return changesetLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("changesets").parameters().method("get").parameters(changesetId).href();
    }

    String all(String namespace, String name) {
      return changesetLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("changesets").parameters().method("getAll").parameters().href();
    }

    public String changeset(String namespace, String name, String revision) {
      return changesetLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("changesets").parameters().method("get").parameters(revision).href();
    }
  }

  public SourceLinks source() {
    return new SourceLinks(uriInfoStore.get());
  }

  static class SourceLinks {
    private final LinkBuilder sourceLinkBuilder;

    SourceLinks(ScmPathInfo uriInfo) {
      sourceLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, SourceRootResource.class);
    }

    String self(String namespace, String name, String revision) {
      return sourceLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("sources").parameters().method("getAll").parameters(revision).href();
    }

    String selfWithoutRevision(String namespace, String name) {
      return sourceLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("sources").parameters().method("getAllWithoutRevision").parameters().href();
    }

    public String source(String namespace, String name, String revision) {
      return sourceLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("sources").parameters().method("get").parameters(revision).href();
    }

    public String sourceWithPath(String namespace, String name, String revision, String path) {
      return addPath(sourceLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("sources").parameters().method("get").parameters(revision, "").href(), path);
    }

    public String content(String namespace, String name, String revision, String path) {
      return addPath(sourceLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("content").parameters().method("get").parameters(revision, "").href(), path);
    }

    // we have to add the file path using URI, so that path separators (aka '/') will not be encoded as '%2F'
    private String addPath(String sourceWithPath, String path) {
      return URI.create(sourceWithPath).resolve(path).toASCIIString();
    }
  }
  public PermissionLinks permission() {
    return new PermissionLinks(uriInfoStore.get());
  }

  static class PermissionLinks {
    private final LinkBuilder permissionLinkBuilder;

    PermissionLinks(ScmPathInfo uriInfo) {
      permissionLinkBuilder = new LinkBuilder(uriInfo, RepositoryRootResource.class, RepositoryResource.class, PermissionRootResource.class);
    }

    String all(String namespace, String name) {
      return permissionLinkBuilder.method("getRepositoryResource").parameters(namespace, name).method("permissions").parameters().method("getAll").parameters().href();
    }

    String create(String repositoryNamespace, String repositoryName) {
      return permissionLinkBuilder.method("getRepositoryResource").parameters(repositoryNamespace, repositoryName).method("permissions").parameters().method("create").parameters().href();
    }

    String self(String repositoryNamespace, String repositoryName, String permissionName) {
      return getLink(repositoryNamespace, repositoryName, permissionName, "get");
    }

    String update(String repositoryNamespace, String repositoryName, String permissionName) {
      return getLink(repositoryNamespace, repositoryName, permissionName, "update");
    }

    String delete(String repositoryNamespace, String repositoryName, String permissionName) {
      return getLink(repositoryNamespace, repositoryName, permissionName, "delete");
    }

    private String getLink(String repositoryNamespace, String repositoryName, String permissionName, String methodName) {
      return permissionLinkBuilder.method("getRepositoryResource").parameters(repositoryNamespace, repositoryName).method("permissions").parameters().method(methodName).parameters(permissionName).href();
    }
  }


  public UIPluginLinks uiPlugin() {
    return new UIPluginLinks(uriInfoStore.get());
  }

  static class UIPluginLinks {
    private final LinkBuilder uiPluginLinkBuilder;

    UIPluginLinks(ScmPathInfo uriInfo) {
      uiPluginLinkBuilder = new LinkBuilder(uriInfo, UIRootResource.class, UIPluginResource.class);
    }

    String self(String id) {
      return uiPluginLinkBuilder.method("plugins").parameters().method("getInstalledPlugin").parameters(id).href();
    }
  }

  public UIPluginCollectionLinks uiPluginCollection() {
    return new UIPluginCollectionLinks(uriInfoStore.get());
  }

  static class UIPluginCollectionLinks {
    private final LinkBuilder uiPluginCollectionLinkBuilder;

    UIPluginCollectionLinks(ScmPathInfo uriInfo) {
      uiPluginCollectionLinkBuilder = new LinkBuilder(uriInfo, UIRootResource.class, UIPluginResource.class);
    }

    String self() {
      return uiPluginCollectionLinkBuilder.method("plugins").parameters().method("getInstalledPlugins").parameters().href();
    }
  }
}
