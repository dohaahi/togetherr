package together.together_project.service.dto;

import java.util.List;


public record PaginationCollection<T>(
        List<T> elementsWithNextCursor,
        long totalElementsCount
) {
    public static <T> PaginationCollection<T> from(List<T> elementsWithNextCursor) {
        return new PaginationCollection<>(elementsWithNextCursor, elementsWithNextCursor.size());
    }

    public List<T> getCurrentData() {
        if (isEndCursor()) {
            return elementsWithNextCursor;
        }

        return elementsWithNextCursor.subList(0, (int) totalElementsCount);
    }

    public long getNextCursor() {
        if (isEndCursor()) {
            return -1;
        }

        return (long) elementsWithNextCursor.get((int) (totalElementsCount - 1));
    }

    private boolean isEndCursor() {
        return elementsWithNextCursor.size() <= totalElementsCount;
    }
}
