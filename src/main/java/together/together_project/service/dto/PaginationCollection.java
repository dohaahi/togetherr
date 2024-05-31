package together.together_project.service.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


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

    @Getter
    @RequiredArgsConstructor
    static class Meta {
        private final int count;
        private final boolean hasMore;
        private final Long lastId;
    }
}
