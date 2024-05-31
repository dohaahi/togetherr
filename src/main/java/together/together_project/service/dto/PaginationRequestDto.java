package together.together_project.service.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class PaginationRequestDto {

    public static int INIT_PAGINATION_COUNT = 20;

    private Long after;
    private Long count;

    public PaginationRequestDto(Long after, Long count) {
        this.count = count == null ? INIT_PAGINATION_COUNT : count;
    }
}