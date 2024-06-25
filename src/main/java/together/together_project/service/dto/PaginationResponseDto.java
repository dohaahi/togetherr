package together.together_project.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaginationResponseDto<T>(
        boolean hasMore,
        long lastId,
        List<T> elements
) {

    public static <T, R extends PaginationCollection<T>> PaginationResponseDto<T> of(R paginationCollection) {
        return new PaginationResponseDto<>(
                paginationCollection.hasMore(),
                paginationCollection.getNextCursor(),
                paginationCollection.getCurrentData()
        );
    }
}
