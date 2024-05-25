package together.together_project.service.dto;

public record PaginationRequestDto(
        Long after,
        Long count
) {
    public static int initPaginationCount = 20;

    public PaginationRequestDto(Long after, Long count) {
        this.after = after;
        this.count = count == null ? initPaginationCount : count;
    }
}