package sonia.scm.api.v2.resources;

import javax.ws.rs.core.UriInfo;

public class UriInfoStore {

  private UriInfo uriInfo;

  public ScmPathInfo get() {
    return () -> uriInfo.getBaseUri();
  }

  public void set(UriInfo uriInfo) {
    if (this.uriInfo != null) {
      throw new IllegalStateException("UriInfo already set");
    }
    this.uriInfo = uriInfo;
  }

}
