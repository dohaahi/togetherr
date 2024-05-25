package together.together_project.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaginationResponseDto<T>(
        List<T> elements,
        long totalElementsCount,
        long nextCursor
) {
}
