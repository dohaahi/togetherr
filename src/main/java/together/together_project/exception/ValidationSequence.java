package together.together_project.exception;

import jakarta.validation.GroupSequence;
import together.together_project.exception.ValidationGroups.NotNullGroup;
import together.together_project.exception.ValidationGroups.PatternGroup;
import together.together_project.exception.ValidationGroups.SizeGroup;

@GroupSequence({NotNullGroup.class, PatternGroup.class, SizeGroup.class})
public interface ValidationSequence {
}
