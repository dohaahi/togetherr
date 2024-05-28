package together.together_project.validator;

import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

public class StudyValidator {

    public static final int MIN_PEOPLE = 2;

    public static void checkMaxPeopleMoreThanMinimum(int maxPeople) {
        if (maxPeople < MIN_PEOPLE) {
            throw new CustomException(ErrorCode.MAX_PEOPLE_UNDER_LIMIT);
        }
    }
}
