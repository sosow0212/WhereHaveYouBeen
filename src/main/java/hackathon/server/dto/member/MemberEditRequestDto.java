package hackathon.server.dto.member;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiOperation(value = "회원 정보 수정 요청")
public class MemberEditRequestDto {
    @ApiModelProperty(value = "name", notes = "유저의 name 값을 입력해주세요.", required = true, example = "홍길동")
    @NotBlank(message = "유저의 name 값을 입력해주세요.")
    private String name;

    @ApiModelProperty(value = "닉네임", notes = "닉네임은 한글 또는 알파벳으로 입력해주세요.", required = true, example = "닉네임1")
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임이 너무 짧습니다.")
    private String nickname;

    @ApiModelProperty(value = "전화번호", notes = "전화번호를 입력해주세요.", required = true, example = "01x-xxxx-xxxx")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",message = "핸드폰 번호의 양식과 맞지 않습니다. 01x-xxxx-xxxx")
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    @ApiModelProperty(value = "address", notes = "유저의 address 값을 입력해주세요.", required = true, example = "경기도 용인시 수지구 ~")
    @NotBlank(message = "유저의 주소 값을 입력해주세요.")
    private String address;
}
