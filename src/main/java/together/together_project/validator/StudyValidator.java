package together.together_project.validator;

import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;
import together.together_project.service.dto.request.StudyPostCreateRequestDto;

public class StudyValidator {

    public static final int MIN_PEOPLE = 2;

    public static void checkMaxPeopleMoreThanMinimum(StudyPostCreateRequestDto request) {
        if (request.maxPeople() < MIN_PEOPLE) {
            throw new CustomException(ErrorCode.MAX_PEOPLE_UNDER_LIMIT);
        }
    }
}
