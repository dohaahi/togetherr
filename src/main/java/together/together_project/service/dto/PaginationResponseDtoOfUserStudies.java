package together.together_project.service.dto;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT_AND_ONE_MORE;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.function.Function;
import together.together_project.service.dto.response.UserStudiesResponseDto;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaginationResponseDtoOfUserStudies(
        boolean hasMore,
        Long lastId,
        UserStudiesResponseDto elements
) {
    public static PaginationResponseDtoOfUserStudies of(
            UserStudiesResponseDto elements,
            Function<UserStudiesResponseDto.MetaStudy, Long> listToId
    ) {
        boolean hasMore = elements.studies().size() >= PAGINATION_COUNT_AND_ONE_MORE;

        Long lastId = -1L;

        if (hasMore) {
            elements.studies()
                    .subList(0, elements.studies().size());
            lastId = listToId.apply(elements.studies().get(elements.studies().size() - 1));
        }

        return new PaginationResponseDtoOfUserStudies(
                hasMore,
                lastId,
                elements
        );
    }
}
