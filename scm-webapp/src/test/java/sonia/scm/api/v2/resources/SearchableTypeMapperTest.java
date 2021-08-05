package sonia.scm.api.v2.resources;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.search.SearchableField;
import sonia.scm.search.SearchableType;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchableTypeMapperTest {

  @Mock
  private SearchableType searchableType;

  @Mock
  private SearchableField searchableField;

  private final SearchableTypeMapper searchableTypeMapper = Mappers.getMapper(SearchableTypeMapper.class);

  @Test
  void shouldMapType() {
    when(searchableType.getName()).thenReturn("HitchhikerType");
    doReturn(ImmutableList.of(searchableField)).when(searchableType).getFields();
    when(searchableField.getName()).thenReturn("HitchhikerField");
    doReturn(Integer.class).when(searchableField).getType();
    final SearchableTypeDto typeDto = searchableTypeMapper.map(searchableType);
    Assertions.assertThat(typeDto.getName()).isEqualTo("HitchhikerType");
    final SearchableFieldDto searchableFieldDto = typeDto.getFields().stream().findFirst().get();
    Assertions.assertThat(searchableFieldDto.getName()).isEqualTo("HitchhikerField");
    Assertions.assertThat(searchableFieldDto.getType()).isEqualTo("number");
  }

}
