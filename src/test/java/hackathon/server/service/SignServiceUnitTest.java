package hackathon.server.service;

import hackathon.server.config.jwt.TokenProvider;
import hackathon.server.dto.sign.LoginRequestDto;
import hackathon.server.dto.sign.SignupRequestDto;
import hackathon.server.dto.sign.TokenResponseDto;
import hackathon.server.dto.token.TokenDto;
import hackathon.server.entity.member.Member;
import hackathon.server.exception.LoginFailureException;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.Member.RefreshTokenRepository;
import hackathon.server.service.sign.SignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static hackathon.server.factory.MemberFactory.createUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SignServiceUnitTest {

    @InjectMocks
    SignService signService;

    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("유저 회원가입 서비스")
    void userSignupTest() {
        // given
        SignupRequestDto req = new SignupRequestDto("user", "user123!", "이름", "닉네임", "user@test.com", "010-1111-1111");

        // when
        signService.userSignup(req);

        // then
        verify(passwordEncoder).encode(req.getPassword());
        verify(memberRepository).save(any());
    }

    @Test
    @DisplayName("가이드 회원가입 서비스")
    void guideSignupTest() {
        // given
        SignupRequestDto req = new SignupRequestDto("user", "user123!", "이름", "닉네임", "user@test.com", "010-1111-1111");

        // when
        signService.guideSignup(req);

        // then
        verify(passwordEncoder).encode(req.getPassword());
        verify(memberRepository).save(any());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void signInExceptionByNoneMemberTest() {
        // given
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(createUser()));

        // when, then
        assertThatThrownBy(() -> signService.signIn(new LoginRequestDto("email", "password")))
                .isInstanceOf(LoginFailureException.class);
    }

    @Test
    @DisplayName("패스워드 검증 테스트")
    void signInExceptionByInvalidPasswordTest() {
        // given
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(createUser()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> signService.signIn(new LoginRequestDto("email", "password")))
                .isInstanceOf(LoginFailureException.class);
    }


}
