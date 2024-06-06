package together.together_project.service.dto;

import java.util.List;


public record PaginationCollection<T>(
        Meta meta,
        List<T> elementsWithNextCursor
) {
    public static <T> PaginationCollection<T> of(boolean hasMore, Long lastId, List<T> elementsWithNextCursor) {
        return new PaginationCollection<>(
                new Meta(elementsWithNextCursor.size(), hasMore, lastId),
                elementsWithNextCursor
        );
    }

    public List<T> getCurrentData() {
        return elementsWithNextCursor.subList(0, meta.count);
    }

    public Long getNextCursor() {
        return meta.lastId;
    }

    public boolean hasMore() {
        return meta.hasMore;
    }

    record Meta(
            int count,
            boolean hasMore,
            Long lastId
    ) {
    }
}
