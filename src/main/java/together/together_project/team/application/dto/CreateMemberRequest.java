package together.together_project.team.application.dto;

public record CreateMemberRequest(
        String username,
        String password,
        String nickname
) {

}
