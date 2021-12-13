package sonia.scm.repository;

import lombok.Data;
import sonia.scm.search.Indexed;
import sonia.scm.search.IndexedType;

import java.io.Serializable;

@IndexedType
@Data
public class IndexedChangeset implements Serializable {

  @Indexed(type = Indexed.Type.STORED_ONLY)
  private final String branch;
  @Indexed
  private final String id;
  @Indexed(defaultQuery = true, highlighted = true)
  private final String description;
  @Indexed
  private final Long date;
}
