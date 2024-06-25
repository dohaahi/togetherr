package together.together_project.service.dto;

import static together.together_project.constant.StudyConstant.PAGINATION_COUNT;

import java.util.List;
import java.util.function.Function;


public record PaginationCollection<T>(
        Meta meta,
        List<T> elementsWithNextCursor
) {
    public static <T> PaginationCollection<T> of(
            List<T> elementsWithNextCursor,
            Function<T, Long> listToId
    ) {
        boolean hasMore = elementsWithNextCursor.size() < PAGINATION_COUNT + 1;

        Long lastId = -1L;
        if (hasMore) {
            elementsWithNextCursor.subList(0, elementsWithNextCursor.size());
            lastId = listToId.apply(elementsWithNextCursor.get(elementsWithNextCursor.size() - 1));
        }

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
